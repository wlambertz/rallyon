import * as React from 'react';

export interface InputProps extends Omit<React.InputHTMLAttributes<HTMLInputElement>, 'size' | 'prefix'> {
  /** Uppercase mono field label rendered above the control. */
  label?: string;
  size?: 'sm' | 'md' | 'lg';
  /** Red border + danger-colored hint. */
  invalid?: boolean;
  /** Helper or error text below the field. */
  hint?: string;
  /** Leading adornment (icon or unit). */
  prefix?: React.ReactNode;
  /** Trailing adornment (unit like "Min", "Pkt"). */
  suffix?: React.ReactNode;
  /** Render the typed value in mono — use for IDs, scores, seeds. */
  mono?: boolean;
}

/** Text input field with mono label, adornments, and error state. */
export function Input(props: InputProps): JSX.Element;
