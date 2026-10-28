import * as React from 'react';
import { SetScore } from './ScoreDisplay';

export interface MatchSide {
  name: string;
  /** Seed number, if seeded. */
  seed?: number | null;
}

export interface MatchCardProps extends React.HTMLAttributes<HTMLDivElement> {
  /** Match ID (e.g. "M-104") — shown in the meta line if provided via round. */
  matchId?: string;
  /** Round/phase label, e.g. "VIERTELFINALE" or "GRUPPE A · R2". */
  round?: string;
  /** Court/field number. */
  court?: number | string;
  /** Scheduled or start time (24h). */
  time?: string;
  status?: 'live' | 'ok' | 'warn' | 'idle';
  /** Exactly two sides (top over bottom). */
  sides: [MatchSide, MatchSide];
  /** Set scores (empty for not-yet-played). */
  sets?: SetScore[];
  onClick?: () => void;
}

/**
 * A single match/tie: two sides, running or final score, court, time, status.
 * @startingPoint section="Domain" subtitle="Match tie card with live score" viewport="360x120"
 */
export function MatchCard(props: MatchCardProps): JSX.Element;
