import * as React from 'react';

export interface BannerProps extends React.HTMLAttributes<HTMLDivElement> {
  tone?: 'info' | 'live' | 'ok' | 'warn' | 'danger';
  /** Bold first line. */
  title?: string;
  /** Body message. */
  children?: React.ReactNode;
  /** Optional trailing action (Button/link). */
  action?: React.ReactNode;
  /** Show a × dismiss control. */
  onDismiss?: () => void;
}

/** Inline system message with a colored left bar. Persistent, not transient. */
export function Banner(props: BannerProps): JSX.Element;
