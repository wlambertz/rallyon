import * as React from 'react';

export interface ProgressBarProps extends React.HTMLAttributes<HTMLDivElement> {
  value?: number;
  max?: number;
  /** Uppercase mono label above the bar. */
  label?: string;
  /** Show the %/count readout at the right. Default true. */
  showValue?: boolean;
  /** Discrete round ticks instead of a continuous fill. */
  segmented?: boolean;
  tone?: 'accent' | 'ink';
}

/** Flat determinate progress — tournament phase / round completion. */
export function ProgressBar(props: ProgressBarProps): JSX.Element;
