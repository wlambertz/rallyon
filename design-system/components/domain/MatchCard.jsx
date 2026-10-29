import React from 'react';
import { ScoreDisplay } from './ScoreDisplay.jsx';

const STATUS = {
  live:   { fg: 'var(--status-live)',   label: 'Läuft',   blink: true },
  ok:     { fg: 'var(--status-ok)',     label: 'Fertig',  blink: false },
  warn:   { fg: 'var(--status-warn)',   label: 'Wartet',  blink: false },
  idle:   { fg: 'var(--status-idle)',   label: 'Geplant', blink: false },
};

/**
 * RallyOn MatchCard — a single tie: two sides, score, court, time, status.
 */
export function MatchCard({
  matchId,
  round,
  court,
  time,
  status = 'idle',
  sides = [{ name: '—', seed: null }, { name: '—', seed: null }],
  sets = [],
  onClick,
  style,
  ...rest
}) {
  const st = STATUS[status] || STATUS.idle;
  const [a, b] = sides;
  const aWon = sets.filter((s) => s.a > s.b).length > sets.filter((s) => s.b > s.a).length;
  const bWon = sets.filter((s) => s.b > s.a).length > sets.filter((s) => s.a > s.b).length;
  const done = status === 'ok';

  const Side = ({ p, win }) => (
    <div style={{ display: 'flex', alignItems: 'center', gap: 8, minWidth: 0 }}>
      {p.seed != null && (
        <span style={{ font: '600 10px/1 var(--font-mono)', color: 'var(--ink-2)', width: 20, flex: '0 0 20px' }}>#{p.seed}</span>
      )}
      <span style={{
        font: `${win && done ? 700 : 500} 14px/1.2 var(--font-sans)`,
        color: done && !win ? 'var(--ink-2)' : 'var(--ink-0)',
        overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap',
      }}>{p.name}</span>
    </div>
  );

  return (
    <div
      onClick={onClick}
      style={{
        display: 'flex', flexDirection: 'column',
        background: 'var(--paper-2)',
        border: 'var(--bw) solid var(--line-strong)',
        borderRadius: 'var(--r-2)',
        borderLeft: status === 'live' ? '3px solid var(--accent)' : 'var(--bw) solid var(--line-strong)',
        cursor: onClick ? 'pointer' : 'default',
        overflow: 'hidden',
        ...style,
      }}
      {...rest}
    >
      {/* header row */}
      <div style={{
        display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 8,
        padding: '7px 12px', borderBottom: '1px solid var(--line)', background: 'var(--paper-1)',
      }}>
        <span style={{ font: '600 10px/1 var(--font-mono)', letterSpacing: 'var(--tracking-caps)', color: 'var(--ink-2)' }}>
          {[round, court && `FELD ${court}`, time].filter(Boolean).join(' · ')}
        </span>
        <span style={{ display: 'inline-flex', alignItems: 'center', gap: 6, font: '600 10px/1 var(--font-mono)', letterSpacing: 'var(--tracking-caps)', color: st.fg }}>
          <span style={{ width: 7, height: 7, borderRadius: '50%', background: st.fg, animation: st.blink ? 'ro-blink 1s steps(1) infinite' : 'none' }} />
          {st.label}
        </span>
      </div>
      {/* body */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 12, padding: '10px 12px' }}>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 8, minWidth: 0, flex: 1 }}>
          <Side p={a} win={aWon} />
          <Side p={b} win={bWon} />
        </div>
        {sets.length > 0 && <ScoreDisplay sets={sets} variant="inline" />}
      </div>
    </div>
  );
}
