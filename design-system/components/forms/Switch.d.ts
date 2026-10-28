import * as React from 'react';

export interface SwitchProps extends Omit<React.InputHTMLAttributes<HTMLInputElement>, 'type'> {
  label?: React.ReactNode;
  checked?: boolean;
  defaultChecked?: boolean;
  disabled?: boolean;
}

/** Mechanical on/off toggle (pill track, hard seam). For a single binary setting. */
export function Switch(props: SwitchProps): JSX.Element;
