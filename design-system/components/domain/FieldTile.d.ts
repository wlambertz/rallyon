import * as React from 'react';

export interface FieldTileProps extends React.HTMLAttributes<HTMLDivElement> {
  /** Court/field number or short label. */
  field: number | string;
  /** Court state — drives the top seam color + chip. */
  state?: 'live' | 'free' | 'next' | 'off';
  /** Current or upcoming match description (players). */
  match?: string | null;
  /** Fallback note when no match (e.g. "verfügbar ab 15:00"). */
  note?: string | null;
  onClick?: () => void;
}

/** A single court in the hall/field manifest — big number + occupancy state. */
export function FieldTile(props: FieldTileProps): JSX.Element;
