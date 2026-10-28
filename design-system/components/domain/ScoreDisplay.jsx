import React from 'react';

/**
 * RallyOn ScoreDisplay — badminton set scores.
 * sets: [{ a, b }, ...]. `variant="board"` uses large Doto numerals
 * (scoreboard / Turnier-TV); `variant="inline"` is compact mono (lists).
 */
export function ScoreDisplay({ sets = [], variant = 'inline', serving = null, style, ...rest }) {
  const aWon = sets.filter((s) => s.a > s.b).length;
  const bWon = sets.filter((s) => s.b > s.a).length;

  if (variant === 'board') {
    const cur = sets[sets.length - 1] || { a: 0, b: 0 };
    return (
      <div style={{ display: 'flex', alignItems: 'center', gap: 18, fontFamily: 'var(--font-dot)', ...style }} {...rest}>
        <span style={{ fontSize: 96, fontWeight: 900, lineHeight: 0.9, color: cur.a >= cur.b ? 'var(--accent)' : 'var(--ink-0)' }}>{String(cur.a).padStart(2,'0')}</span>
        <span style={{ fontSize: 64, fontWeight: 700, color: 'var(--ink-2)' }}>:</span>
        <span style={{ fontSize: 96, fontWeight: 900, lineHeight: 0.9, color: cur.b > cur.a ? 'var(--accent)' : 'var(--ink-0)' }}>{String(cur.b).padStart(2,'0')}</span>
      </div>
    );
  }

  // inline
  return (
    <div style={{ display: 'inline-flex', gap: 6, ...style }} {...rest}>
      {sets.map((s, i) => {
        const done = s.a >= 21 || s.b >= 21 || i < sets.length - 1;
        const aw = s.a > s.b;
        return (
          <div key={i} style={{
            display: 'flex', flexDirection: 'column', alignItems: 'center',
            minWidth: 26, padding: '2px 4px',
            border: '1px solid var(--line)', borderRadius: 'var(--r-1)',
            background: done ? 'var(--paper-1)' : 'var(--paper-0)',
            font: '600 13px/1.3 var(--font-mono)',
          }}>
            <span style={{ color: aw ? 'var(--ink-0)' : 'var(--ink-2)', fontWeight: aw ? 700 : 500 }}>{s.a}</span>
            <span style={{ color: !aw ? 'var(--ink-0)' : 'var(--ink-2)', fontWeight: !aw ? 700 : 500 }}>{s.b}</span>
          </div>
        );
      })}
      {sets.length > 0 && (
        <div style={{
          display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
          minWidth: 24, marginLeft: 2, paddingLeft: 6, borderLeft: '1px solid var(--line)',
          font: '700 15px/1.1 var(--font-mono)', color: 'var(--ink-0)',
        }}>
          <span style={{ color: aWon > bWon ? 'var(--ink-0)' : 'var(--ink-1)' }}>{aWon}</span>
          <span style={{ color: bWon > aWon ? 'var(--ink-0)' : 'var(--ink-1)' }}>{bWon}</span>
        </div>
      )}
    </div>
  );
}
