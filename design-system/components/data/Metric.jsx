import React from 'react';

/**
 * RallyOn Metric — a single KPI readout for the ops board.
 * Big mono value, uppercase label, optional delta + unit.
 */
export function Metric({ label, value, unit, delta, deltaTone = 'idle', accent = false, style, ...rest }) {
  const deltaColors = {
    up: 'var(--status-ok)', down: 'var(--status-danger)', idle: 'var(--ink-2)',
  };
  return (
    <div
      style={{
        display: 'flex', flexDirection: 'column', gap: 6,
        padding: '14px 16px',
        background: 'var(--paper-2)',
        border: 'var(--bw) solid var(--line-strong)',
        borderRadius: 'var(--r-2)',
        borderLeft: accent ? '3px solid var(--accent)' : 'var(--bw) solid var(--line-strong)',
        minWidth: 120,
        ...style,
      }}
      {...rest}
    >
      <span style={{ font: '600 10px/1 var(--font-mono)', letterSpacing: 'var(--tracking-caps)', textTransform: 'uppercase', color: 'var(--ink-2)' }}>{label}</span>
      <div style={{ display: 'flex', alignItems: 'baseline', gap: 6 }}>
        <span style={{ font: '600 30px/1 var(--font-mono)', color: 'var(--ink-0)', letterSpacing: '-0.02em' }}>{value}</span>
        {unit && <span style={{ font: '500 12px/1 var(--font-mono)', color: 'var(--ink-2)' }}>{unit}</span>}
      </div>
      {delta != null && (
        <span style={{ font: '500 11px/1 var(--font-mono)', color: deltaColors[deltaTone] }}>{delta}</span>
      )}
    </div>
  );
}
