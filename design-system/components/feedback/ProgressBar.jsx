import React from 'react';

/**
 * RallyOn ProgressBar — flat determinate bar for phase/round progress.
 * segmented=true renders discrete round ticks instead of a continuous fill.
 */
export function ProgressBar({ value = 0, max = 100, label, showValue = true, segmented = false, tone = 'ink', style, ...rest }) {
  const pct = Math.max(0, Math.min(100, (value / max) * 100));
  const fill = tone === 'accent' ? 'var(--accent)' : 'var(--ink-0)';

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 6, ...style }} {...rest}>
      {(label || showValue) && (
        <div style={{ display: 'flex', justifyContent: 'space-between', font: '600 10px/1 var(--font-mono)', letterSpacing: 'var(--tracking-caps)', textTransform: 'uppercase', color: 'var(--ink-2)' }}>
          {label && <span>{label}</span>}
          {showValue && <span style={{ color: 'var(--ink-0)' }}>{segmented ? `${value}/${max}` : `${Math.round(pct)}%`}</span>}
        </div>
      )}
      {segmented ? (
        <div style={{ display: 'flex', gap: 3 }}>
          {Array.from({ length: max }).map((_, i) => (
            <div key={i} style={{
              flex: 1, height: 8,
              background: i < value ? fill : 'var(--paper-0)',
              border: '1px solid var(--line-strong)',
              borderRadius: '1px',
            }} />
          ))}
        </div>
      ) : (
        <div style={{ height: 8, background: 'var(--paper-0)', border: 'var(--bw) solid var(--line-strong)', borderRadius: '1px', overflow: 'hidden' }}>
          <div style={{ width: `${pct}%`, height: '100%', background: fill, transition: 'width var(--dur-2) var(--ease-standard)' }} />
        </div>
      )}
    </div>
  );
}
