import * as React from 'react';

export interface CardProps extends React.HTMLAttributes<HTMLElement> {
  /** Uppercase mono label above the title (e.g. "FELD-MANIFEST"). */
  eyebrow?: string;
  /** Module title in Space Grotesk. */
  title?: string;
  /** Node placed at the right of the header (usually an IconButton or Button). */
  action?: React.ReactNode;
  /** Adds the hairline lift shadow. Default flat. */
  raised?: boolean;
  /** Toggle body padding. Set false for flush tables/lists. */
  padded?: boolean;
  /** Adds a 3px accent seam on the left edge (use sparingly for "live" modules). */
  accent?: boolean;
  children?: React.ReactNode;
}

/**
 * Module container — the primary structural unit of the ops board.
 * @startingPoint section="Data" subtitle="Module card with eyebrow header" viewport="700x260"
 */
export function Card(props: CardProps): JSX.Element;
