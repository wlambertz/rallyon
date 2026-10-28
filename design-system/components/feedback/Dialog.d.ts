import * as React from 'react';

export interface DialogProps {
  open: boolean;
  /** Space Grotesk title. */
  title?: string;
  /** Uppercase mono eyebrow above the title. */
  eyebrow?: string;
  onClose?: () => void;
  /** Footer node — usually a right-aligned Button row. */
  footer?: React.ReactNode;
  /** Panel width in px. Default 460. */
  width?: number;
  children?: React.ReactNode;
}

/** Modal overlay: scrim + centered hard-seam panel. Closes on Esc / scrim click. */
export function Dialog(props: DialogProps): JSX.Element | null;
