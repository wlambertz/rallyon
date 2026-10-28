import React from 'react';

/**
 * RallyOn Tag — square metadata chip for categories, formats, seeds.
 * Neutral by default; `accent` for the one thing worth highlighting.
 */
export function Tag({ children, accent = false, removable = false, onRemove, style, ...rest }) {
  return (
    <span
      style={{
        display: 'inline-flex', alignItems: 'center', gap: 6,
        height: 20, padding: '0 8px',
        borderRadius: 'var(--r-1)',
        border: `1px solid ${accent ? 'var(--accent-line)' : 'var(--line)'}`,
        background: accent ? 'var(--accent-soft)' : 'var(--paper-1)',
        color: accent ? 'var(--accent-press)' : 'var(--ink-1)',
        font: '500 11px/1 var(--font-mono)',
        letterSpacing: '0.02em',
        whiteSpace: 'nowrap',
        ...style,
      }}
      {...rest}
    >
      {children}
      {removable && (
        <button
          type="button"
          onClick={onRemove}
          aria-label="Entfernen"
          style={{
            border: 'none', background: 'transparent', cursor: 'pointer',
            color: 'inherit', font: '700 11px/1 var(--font-mono)', padding: 0, marginRight: -2,
          }}
        >×</button>
      )}
    </span>
  );
}
