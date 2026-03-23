type PencilTarget =
  | 'foundations/workflow'
  | 'components/button'
  | 'components/status-tag'
  | 'layout/navigation'
  | 'modules/dashboard-action-card'
  | 'screens/login'
  | 'screens/dashboard'
  | 'screens/events'
  | 'screens/settings'

interface PencilArtifact {
  readonly penFile: string
  readonly focus: string
  readonly previewFile?: string
  readonly syncPaths?: readonly string[]
}

const PENCIL_ARTIFACTS: Record<PencilTarget, PencilArtifact> = {
  'foundations/workflow': {
    penFile: 'application/organizer/design/pencil/organizer-ui.lib.pen',
    focus: 'library-canvas',
    syncPaths: [
      'application/organizer/src/styles/settings/_tokens.scss',
      'application/organizer/src/app/rallyonpreset.ts',
      'application/organizer/src/styles/elements/_typography.scss',
    ],
  },
  'components/button': {
    penFile: 'application/organizer/design/pencil/organizer-ui.lib.pen',
    focus: 'button-primary',
    syncPaths: ['application/organizer/src/styles/settings/_tokens.scss'],
  },
  'components/status-tag': {
    penFile: 'application/organizer/design/pencil/organizer-ui.lib.pen',
    focus: 'status-tag-live',
    syncPaths: [
      'application/organizer/src/styles/settings/_tokens.scss',
      'application/organizer/src/app/rallyonpreset.ts',
    ],
  },
  'layout/navigation': {
    penFile: 'application/organizer/design/pencil/screens/navigation.pen',
    focus: 'navigation-screen',
    syncPaths: ['application/organizer/src/app/layout/navigation/navigation.component.scss'],
  },
  'modules/dashboard-action-card': {
    penFile: 'application/organizer/design/pencil/organizer-ui.lib.pen',
    focus: 'dashboard-action-card',
    syncPaths: ['application/organizer/src/app/features/dashboard/dashboard.component.scss'],
  },
  'screens/login': {
    penFile: 'application/organizer/design/pencil/screens/login.pen',
    focus: 'login-screen',
  },
  'screens/dashboard': {
    penFile: 'application/organizer/design/pencil/screens/dashboard.pen',
    focus: 'dashboard-screen',
  },
  'screens/events': {
    penFile: 'application/organizer/design/pencil/screens/events.pen',
    focus: 'events-screen',
  },
  'screens/settings': {
    penFile: 'application/organizer/design/pencil/screens/settings.pen',
    focus: 'settings-screen',
  },
}

function toInlineCodeList(paths: readonly string[]): string {
  return paths.map((path) => `\`${path}\``).join(', ')
}

export function pencilArtifact(target: PencilTarget): PencilArtifact {
  return PENCIL_ARTIFACTS[target]
}

export function pencilArtifactNote(target: PencilTarget): string {
  const artifact = pencilArtifact(target)
  const lines = [
    `Pencil source: \`${artifact.penFile}\``,
    `Focus object: \`${artifact.focus}\``,
  ]

  if (artifact.previewFile) {
    lines.push(`Preview export: \`${artifact.previewFile}\``)
  }

  if (artifact.syncPaths && artifact.syncPaths.length > 0) {
    lines.push(`Code sync surfaces: ${toInlineCodeList(artifact.syncPaths)}`)
  }

  return lines.join('\n\n')
}

export { PENCIL_ARTIFACTS }
