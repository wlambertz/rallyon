import * as React from 'react';

export interface StatusPillProps extends React.HTMLAttributes<HTMLSpanElement> {
  /** Operational status — drives color and blink. */
  tone?: 'live' | 'ok' | 'warn' | 'danger' | 'idle';
  /** UPPERCASE label (kept short: LIVE, FERTIG, WARTET, KONFLIKT, GEPLANT). */
  label: React.ReactNode;
  /** Show the leading status dot. Default true. `live` dot blinks. */
  dot?: boolean;
  /** Solid fill instead of soft tint — for high-emphasis / dark surfaces. */
  solid?: boolean;
}

/** Operational status chip: color + dot + uppercase mono label. */
export function StatusPill(props: StatusPillProps): JSX.Element;
