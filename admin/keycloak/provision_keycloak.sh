#!/usr/bin/env bash
set -eo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/../.." && pwd)"

if command -v cygpath >/dev/null 2>&1; then
  COMPOSE_FILE="$(cygpath -w "$ROOT_DIR/infrastructure/local/docker-compose.yml")"
else
  COMPOSE_FILE="$ROOT_DIR/infrastructure/local/docker-compose.yml"
fi

KEYCLOAK_SERVER="${KEYCLOAK_SERVER:-http://localhost:8081}"
REALM="${RALLYON_REALM:-rallyon}"
CLIENT_ID="${RALLYON_CLIENT_ID:-rallyon-api}"
CLIENT_SECRET="${RALLYON_CLIENT_SECRET:?RALLYON_CLIENT_SECRET must be set}"
REDIRECT_URIS="${RALLYON_REDIRECT_URIS:-http://localhost:8080/*}"
USER_ID_CLAIM="${RALLYON_USER_ID_CLAIM:-rallyon_user_id}"
DEV_USERNAME="${RALLYON_DEV_USERNAME:-dev.organizer}"
DEV_PASSWORD="${RALLYON_DEV_PASSWORD:-DevOrganizer!1}"
DEV_USER_ID="${RALLYON_DEV_USER_ID:-42}"
DEV_EMAIL="${RALLYON_DEV_EMAIL:-${DEV_USERNAME}@local.test}"
KEYCLOAK_ADMIN="${KEYCLOAK_ADMIN:-admin}"
KEYCLOAK_ADMIN_PASSWORD="${KEYCLOAK_ADMIN_PASSWORD:-admin}"

command -v jq >/dev/null 2>&1 ||
  { echo "jq is required to provision Keycloak user profile and protocol mappers."; exit 1; }

MSYS_NO_PATHCONV=1 docker compose -f "$COMPOSE_FILE" ps keycloak |
  grep -q "Up" ||
  { echo "Keycloak container is not running. Start the compose stack first."; exit 1; }

kcadm() {
  MSYS_NO_PATHCONV=1 docker compose -f "$COMPOSE_FILE" exec -T keycloak /opt/keycloak/bin/kcadm.sh "$@"
}

login() {
  kcadm config credentials \
    --server "$KEYCLOAK_SERVER" \
    --realm master \
    --user "$KEYCLOAK_ADMIN" \
    --password "$KEYCLOAK_ADMIN_PASSWORD"
}

ensure_realm() {
  if ! kcadm get "realms/$REALM" >/dev/null 2>&1; then
    kcadm create realms \
      -s "realm=$REALM" \
      -s "enabled=true" \
      -s "displayName=RallyOn ($REALM)"
  fi
}

ensure_role() {
  local role="$1"
  kcadm get "realms/$REALM/roles/$role" >/dev/null 2>&1 ||
    kcadm create "realms/$REALM/roles" -s "name=$role"
}

ensure_user_profile_attribute() {
  local profile_json updated_json container_profile_json
  profile_json=$(mktemp)
  updated_json=$(mktemp)
  container_profile_json="/tmp/rallyon-user-profile.json"

  kcadm get "realms/$REALM/users/profile" > "$profile_json"

  jq --arg user_id_claim "$USER_ID_CLAIM" '
    if any(.attributes[]?; .name == $user_id_claim) then .
    else .attributes += [{
      name: $user_id_claim,
      displayName: "RallyOn User Id",
      permissions: {
        view: ["admin", "user"],
        edit: ["admin"]
      },
      multivalued: false,
      group: "user-metadata",
      validations: {
        length: {
          max: 255
        }
      }
    }]
    end
  ' "$profile_json" > "$updated_json"

  MSYS_NO_PATHCONV=1 docker cp "$updated_json" "keycloak:$container_profile_json" >/dev/null
  kcadm update "realms/$REALM/users/profile" -f "$container_profile_json"
  MSYS_NO_PATHCONV=1 docker compose -f "$COMPOSE_FILE" exec -T keycloak rm -f "$container_profile_json" >/dev/null 2>&1 || true

  rm -f "$profile_json" "$updated_json"
}

mapper_id_by_name() {
  local client_oid="$1" mapper_name="$2"

  kcadm get "clients/$client_oid/protocol-mappers/models" -r "$REALM" |
    jq -r --arg mapper_name "$mapper_name" '.[] | select(.name == $mapper_name) | .id' |
    head -n1
}

ensure_audience_mapper() {
  local client_oid="$1" mapper_name="rallyon-api-audience" mapper_oid
  mapper_oid=$(mapper_id_by_name "$client_oid" "$mapper_name")

  if [[ -n "$mapper_oid" ]]; then
    kcadm update "clients/$client_oid/protocol-mappers/models/$mapper_oid" -r "$REALM" \
      -s "name=$mapper_name" \
      -s "protocol=openid-connect" \
      -s "protocolMapper=oidc-audience-mapper" \
      -s "config.\"included.client.audience\"=$CLIENT_ID" \
      -s "config.\"access.token.claim\"=true" \
      -s "config.\"id.token.claim\"=false" \
      -s "config.\"userinfo.token.claim\"=false"
  else
    kcadm create "clients/$client_oid/protocol-mappers/models" -r "$REALM" \
      -s "name=$mapper_name" \
      -s "protocol=openid-connect" \
      -s "protocolMapper=oidc-audience-mapper" \
      -s "config.\"included.client.audience\"=$CLIENT_ID" \
      -s "config.\"access.token.claim\"=true" \
      -s "config.\"id.token.claim\"=false" \
      -s "config.\"userinfo.token.claim\"=false" >/dev/null
  fi
}

ensure_user_id_mapper() {
  local client_oid="$1" mapper_name="rallyon-user-id" mapper_oid
  mapper_oid=$(mapper_id_by_name "$client_oid" "$mapper_name")

  if [[ -n "$mapper_oid" ]]; then
    kcadm update "clients/$client_oid/protocol-mappers/models/$mapper_oid" -r "$REALM" \
      -s "name=$mapper_name" \
      -s "protocol=openid-connect" \
      -s "protocolMapper=oidc-usermodel-attribute-mapper" \
      -s "config.\"user.attribute\"=$USER_ID_CLAIM" \
      -s "config.\"claim.name\"=$USER_ID_CLAIM" \
      -s "config.\"jsonType.label\"=long" \
      -s "config.\"access.token.claim\"=true" \
      -s "config.\"id.token.claim\"=false" \
      -s "config.\"userinfo.token.claim\"=false"
  else
    kcadm create "clients/$client_oid/protocol-mappers/models" -r "$REALM" \
      -s "name=$mapper_name" \
      -s "protocol=openid-connect" \
      -s "protocolMapper=oidc-usermodel-attribute-mapper" \
      -s "config.\"user.attribute\"=$USER_ID_CLAIM" \
      -s "config.\"claim.name\"=$USER_ID_CLAIM" \
      -s "config.\"jsonType.label\"=long" \
      -s "config.\"access.token.claim\"=true" \
      -s "config.\"id.token.claim\"=false" \
      -s "config.\"userinfo.token.claim\"=false" >/dev/null
  fi
}

ensure_protocol_mappers() {
  local client_oid="$1"
  ensure_audience_mapper "$client_oid"
  ensure_user_id_mapper "$client_oid"
}

ensure_client() {
  local client_id
  client_id=$(kcadm get clients -r "$REALM" --query "clientId=$CLIENT_ID" \
      | awk -F'"' '/"id"[ ]*:[ ]*"/ {print $4; exit}')

  if [[ -n "$client_id" ]]; then
    kcadm update "clients/$client_id" -r "$REALM" \
      -s "redirectUris=[\"${REDIRECT_URIS//,/\",\"}\"]" \
      -s "protocol=openid-connect" \
      -s "standardFlowEnabled=true" \
      -s "serviceAccountsEnabled=true" \
      -s "directAccessGrantsEnabled=true" \
      -s "publicClient=false" \
      -s "secret=$CLIENT_SECRET"
  else
    client_id=$(kcadm create clients -r "$REALM" \
      -s "clientId=$CLIENT_ID" \
      -s "redirectUris=[\"${REDIRECT_URIS//,/\",\"}\"]" \
      -s "protocol=openid-connect" \
      -s "standardFlowEnabled=true" \
      -s "serviceAccountsEnabled=true" \
      -s "directAccessGrantsEnabled=true" \
      -s "publicClient=false" \
      -s "secret=$CLIENT_SECRET" \
      --id)
    if [[ -z "$client_id" ]]; then
      client_id=$(kcadm get clients -r "$REALM" --query "clientId=$CLIENT_ID" \
          | awk -F'"' '/"id"[ ]*:[ ]*"/ {print $4; exit}')
    fi
  fi

  if [[ -z "$client_id" ]]; then
    echo "Failed to resolve client id for $CLIENT_ID" >&2
    exit 1
  fi

  echo "$client_id"
}

assign_realm_roles() {
  local user_id="$1"; shift
  for role in "$@"; do
    kcadm add-roles --uid "$user_id" -r "$REALM" --rolename "$role" >/dev/null 2>&1 || true
  done
}

ensure_user() {
  local user_id
  user_id=$(kcadm get users \
      -r "$REALM" \
      --query "username=$DEV_USERNAME" \
      --fields id \
      --format csv \
      --noquotes 2>/dev/null | tr -d '\r' | head -n1)

  if [[ -z "$user_id" ]]; then
    user_id=$(kcadm create users -r "$REALM" \
      -s "username=$DEV_USERNAME" \
      -s "enabled=true" \
      -s "firstName=${DEV_USERNAME%%.*}" \
      -s "lastName=${DEV_USERNAME##*.}" \
      -s "email=$DEV_EMAIL" \
      -s "emailVerified=true" \
      -s 'requiredActions=[]' \
      -s "attributes={\"$USER_ID_CLAIM\":[\"$DEV_USER_ID\"]}" \
      --id)
  fi

  kcadm update "users/$user_id" -r "$REALM" \
    -s "enabled=true" \
    -s "firstName=${DEV_USERNAME%%.*}" \
    -s "lastName=${DEV_USERNAME##*.}" \
    -s "email=$DEV_EMAIL" \
    -s "emailVerified=true" \
    -s 'requiredActions=[]' \
    -s "attributes={\"$USER_ID_CLAIM\":[\"$DEV_USER_ID\"]}"

  kcadm set-password \
    --target-realm "$REALM" \
    --userid "$user_id" \
    --new-password "$DEV_PASSWORD" >/dev/null

  assign_realm_roles "$user_id" rallyon-organizer
}

ensure_service_account() {
  local client_id="$1" svc_user svc_id
  svc_user=$(kcadm get "realms/$REALM/clients/$client_id/service-account-user")
  svc_id=$(echo "$svc_user" | awk -F'"' '/"id"[[:space:]]*:/ {print $4; exit}')
  if [[ -n "$svc_id" ]]; then
    assign_realm_roles "$svc_id" rallyon-service
  fi
}

main() {
  login
  ensure_realm

  for role in rallyon-organizer rallyon-participants rallyon-audience rallyon-service; do
    ensure_role "$role"
  done

  local client_oid
  client_oid=$(ensure_client)
  ensure_user_profile_attribute
  ensure_protocol_mappers "$client_oid"
  ensure_service_account "$client_oid"
  ensure_user

  echo "Keycloak realm '$REALM' provisioned successfully."
}

main "$@"
