import * as React from 'react';

export interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  /** Visual role. `primary` is ink-filled with the print-block shadow (no accent color). */
  variant?: 'primary' | 'secondary' | 'ghost' | 'danger';
  /** Control height. Use `lg` (44px) for touch-critical actions. */
  size?: 'sm' | 'md' | 'lg';
  /** Stretch to full container width. */
  block?: boolean;
  disabled?: boolean;
  /** Icon node rendered before the label (Lucide, 16px). */
  iconLeft?: React.ReactNode;
  /** Icon node rendered after the label. */
  iconRight?: React.ReactNode;
  children?: React.ReactNode;
}

/**
 * Primary operational action control.
 * @startingPoint section="Forms" subtitle="Action button — 4 variants, 3 sizes" viewport="700x220"
 */
export function Button(props: ButtonProps): JSX.Element;
