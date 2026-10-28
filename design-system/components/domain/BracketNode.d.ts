import * as React from 'react';

export interface BracketCompetitor {
  name?: string;
  seed?: number | null;
  /** Aggregate score / sets won shown at the right. */
  score?: number | string | null;
  /** Marks this competitor as the winner (highlighted, opponent dims). */
  winner?: boolean;
}

export interface BracketNodeProps extends React.HTMLAttributes<HTMLDivElement> {
  /** Exactly two competitors (top, bottom). */
  sides: [BracketCompetitor, BracketCompetitor];
  /** Optional slot label ("VF 1", "HALBFINALE"). */
  label?: string | null;
  /** Currently-playing node — adds accent seam. */
  live?: boolean;
  onClick?: () => void;
}

/** A single KO-bracket slot (two competitors + scores). Connectors drawn by the layout. */
export function BracketNode(props: BracketNodeProps): JSX.Element;
