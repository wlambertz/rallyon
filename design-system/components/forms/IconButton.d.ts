import * as React from 'react';

export interface IconButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'ghost';
  size?: 'sm' | 'md' | 'lg';
  /** Toggled/selected state — fills with accent-soft. */
  active?: boolean;
  disabled?: boolean;
  /** Accessible label (also the tooltip). Required. */
  title: string;
  /** A single icon glyph (Lucide, sized to fit). */
  children: React.ReactNode;
}

/** Square icon-only control for toolbars, table rows, and pagination. */
export function IconButton(props: IconButtonProps): JSX.Element;
