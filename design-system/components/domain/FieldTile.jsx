import React from 'react';

const STATE = {
  live: { fg: 'var(--status-live)', label: 'LÄUFT', blink: true },
  free: { fg: 'var(--status-ok)',   label: 'FREI',  blink: false },
  next: { fg: 'var(--status-warn)', label: 'GLEICH', blink: false },
  off:  { fg: 'var(--status-idle)', label: 'GESPERRT', blink: false },
};

/**
 * RallyOn FieldTile — a court in the hall manifest. Big field number,
 * current occupant / free, state chip.
 */
export function FieldTile({ field, state = 'free', match = null, note = null, onClick, style, ...rest }) {
  const s = STATE[state] || STATE.free;
  const active = state === 'live';
  return (
    <div
      onClick={onClick}
      style={{
        display: 'flex', flexDirection: 'column',
        background: 'var(--paper-2)',
        border: `var(--bw) solid var(--line-strong)`,
        borderRadius: 'var(--r-2)',
        borderTop: `3px solid ${s.fg}`,
        cursor: onClick ? 'pointer' : 'default',
        minWidth: 150, overflow: 'hidden',
        opacity: state === 'off' ? 0.65 : 1,
        ...style,
      }}
      {...rest}
    >
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end', padding: '8px 12px 4px', minHeight: 18 }}>
        <span style={{ display: 'inline-flex', alignItems: 'center', gap: 5, font: '600 9px/1 var(--font-mono)', letterSpacing: 'var(--tracking-caps)', color: s.fg, whiteSpace: 'nowrap' }}>
          <span style={{ width: 6, height: 6, borderRadius: '50%', background: s.fg, animation: s.blink ? 'ro-blink 1s steps(1) infinite' : 'none' }} />
          {s.label}
        </span>
      </div>
      <div style={{ display: 'flex', alignItems: 'baseline', gap: 7, padding: '0 12px' }}>
        <span style={{ font: '600 10px/1 var(--font-mono)', letterSpacing: 'var(--tracking-caps)', color: 'var(--ink-2)' }}>FELD</span>
        <span style={{ font: '700 34px/1 var(--font-display)', letterSpacing: '-0.03em', color: 'var(--ink-0)' }}>{field}</span>
      </div>
      <div style={{ padding: '6px 12px 10px', borderTop: '1px solid var(--line)', marginTop: 8, minHeight: 30 }}>
        {match ? (
          <span style={{ font: '500 12px/1.3 var(--font-sans)', color: 'var(--ink-1)' }}>{match}</span>
        ) : (
          <span style={{ font: '400 12px/1.3 var(--font-sans)', color: 'var(--ink-2)' }}>{note || '—'}</span>
        )}
      </div>
    </div>
  );
}
