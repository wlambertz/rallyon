import * as React from 'react';

export interface TabItem {
  id: string;
  label: React.ReactNode;
  /** Optional count badge (e.g. open matches in that view). */
  count?: number;
}

export interface TabsProps extends Omit<React.HTMLAttributes<HTMLDivElement>, 'onChange'> {
  tabs: TabItem[];
  /** Controlled active id. */
  value?: string;
  defaultValue?: string;
  onChange?: (id: string) => void;
}

/** Underline-seam tab bar (uppercase mono labels + optional count badge). */
export function Tabs(props: TabsProps): JSX.Element;
