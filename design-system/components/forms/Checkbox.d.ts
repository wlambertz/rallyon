import * as React from 'react';

export interface CheckboxProps extends Omit<React.InputHTMLAttributes<HTMLInputElement>, 'type'> {
  /** Text label to the right of the box. */
  label?: React.ReactNode;
  checked?: boolean;
  defaultChecked?: boolean;
  disabled?: boolean;
}

/** Square checkbox with accent fill and hard seam. Works controlled or uncontrolled. */
export function Checkbox(props: CheckboxProps): JSX.Element;
