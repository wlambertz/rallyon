import * as React from 'react';

export interface TagProps extends React.HTMLAttributes<HTMLSpanElement> {
  children: React.ReactNode;
  /** Accent-soft highlight. Use for at most one tag in a group. */
  accent?: boolean;
  /** Show a × remove affordance. */
  removable?: boolean;
  onRemove?: () => void;
}

/** Square metadata chip for formats, categories, seeds, filters. */
export function Tag(props: TagProps): JSX.Element;
