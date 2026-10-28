import * as React from 'react';

export interface SelectOption {
  value: string;
  label: string;
}

export interface SelectProps extends Omit<React.SelectHTMLAttributes<HTMLSelectElement>, 'size'> {
  label?: string;
  /** Options as plain strings or `{value,label}` objects. */
  options: (string | SelectOption)[];
  size?: 'sm' | 'md' | 'lg';
  invalid?: boolean;
  hint?: string;
}

/** Native single-select dropdown styled with the hard-seam control shell. */
export function Select(props: SelectProps): JSX.Element;
