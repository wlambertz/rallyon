import React from 'react';

/**
 * RallyOn BracketNode — a single KO-bracket slot (two competitors + scores).
 * Connectors are drawn by the layout, not the node.
 */
export function BracketNode({ sides = [{}, {}], label = null, live = false, onClick, style, ...rest }) {
  const winner = sides.findIndex((s) => s.winner);

  const Row = ({ p, idx }) => {
    const isWinner = winner === idx;
    const decided = winner !== -1;
    return (
      <div style={{
        display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 8,
        padding: '5px 10px',
        borderBottom: idx === 0 ? '1px solid var(--line)' : 'none',
        background: isWinner ? 'var(--paper-0)' : 'transparent',
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 7, minWidth: 0 }}>
          <span style={{ font: '600 9px/1 var(--font-mono)', color: 'var(--ink-2)', width: 16, flex: '0 0 16px' }}>{p.seed != null ? p.seed : '·'}</span>
          <span style={{
            font: `${isWinner ? 700 : 500} 12px/1.2 var(--font-sans)`,
            color: decided && !isWinner ? 'var(--ink-2)' : 'var(--ink-0)',
            overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap',
          }}>{p.name || '—'}</span>
        </div>
        <span style={{ font: '700 12px/1 var(--font-mono)', color: 'var(--ink-0)' }}>{p.score != null ? p.score : ''}</span>
      </div>
    );
  };

  return (
    <div
      onClick={onClick}
      style={{
        width: 200,
        background: 'var(--paper-2)',
        border: 'var(--bw) solid var(--line-strong)',
        borderRadius: 'var(--r-1)',
        borderLeft: live ? '3px solid var(--accent)' : 'var(--bw) solid var(--line-strong)',
        cursor: onClick ? 'pointer' : 'default',
        overflow: 'hidden',
        ...style,
      }}
      {...rest}
    >
      {label && (
        <div style={{ padding: '3px 10px', borderBottom: '1px solid var(--line)', background: 'var(--paper-1)', font: '600 9px/1 var(--font-mono)', letterSpacing: 'var(--tracking-caps)', color: 'var(--ink-2)' }}>{label}</div>
      )}
      <Row p={sides[0] || {}} idx={0} />
      <Row p={sides[1] || {}} idx={1} />
    </div>
  );
}
