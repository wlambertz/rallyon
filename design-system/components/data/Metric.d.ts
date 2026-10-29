import * as React from 'react';

export interface MetricProps extends React.HTMLAttributes<HTMLDivElement> {
  /** Uppercase mono label. */
  label: string;
  /** Primary value — kept in mono, tabular. */
  value: React.ReactNode;
  /** Optional unit shown after the value. */
  unit?: string;
  /** Optional secondary delta/context line. */
  delta?: React.ReactNode;
  deltaTone?: 'up' | 'down' | 'idle';
  /** Left accent seam for the headline metric. */
  accent?: boolean;
}

/** Single KPI readout for the ops-board status strip. */
export function Metric(props: MetricProps): JSX.Element;
