import * as React from 'react';

export interface SetScore {
  /** Points for side A (top). */
  a: number;
  /** Points for side B (bottom). */
  b: number;
}

export interface ScoreDisplayProps extends React.HTMLAttributes<HTMLDivElement> {
  /** Ordered set scores. Last entry is the current/most-recent set. */
  sets: SetScore[];
  /** `inline` = compact mono cells (lists/tables); `board` = large Doto numerals (Turnier-TV). */
  variant?: 'inline' | 'board';
  /** Optional serving side indicator ('a' | 'b'). */
  serving?: 'a' | 'b' | null;
}

/** Badminton set scores — two stacked rows (side A over side B). */
export function ScoreDisplay(props: ScoreDisplayProps): JSX.Element;
