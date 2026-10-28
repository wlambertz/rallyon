import React from 'react';

/**
 * RallyOn DataTable — dense, mono-aligned tabular data with hard header seam.
 * columns: [{ key, header, width, align, mono, render }]
 */
export function DataTable({ columns = [], rows = [], onRowClick, getRowKey, compact = false, style, ...rest }) {
  const [hover, setHover] = React.useState(-1);
  const rowH = compact ? 34 : 42;

  return (
    <div style={{ overflowX: 'auto', ...style }} {...rest}>
      <table style={{ width: '100%', borderCollapse: 'collapse', fontFamily: 'var(--font-sans)' }}>
        <thead>
          <tr>
            {columns.map((c) => (
              <th key={c.key} style={{
                textAlign: c.align || 'left',
                padding: '0 12px', height: 34,
                width: c.width, whiteSpace: 'nowrap',
                font: '600 10px/1 var(--font-mono)', letterSpacing: 'var(--tracking-caps)', textTransform: 'uppercase',
                color: 'var(--ink-2)',
                borderBottom: 'var(--bw) solid var(--line-strong)',
                background: 'var(--paper-1)',
              }}>{c.header}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map((r, i) => (
            <tr
              key={getRowKey ? getRowKey(r, i) : i}
              onClick={onRowClick ? () => onRowClick(r, i) : undefined}
              onMouseEnter={() => setHover(i)}
              onMouseLeave={() => setHover(-1)}
              style={{
                cursor: onRowClick ? 'pointer' : 'default',
                background: hover === i ? 'var(--paper-1)' : 'transparent',
                boxShadow: hover === i && onRowClick ? 'inset 2px 0 0 var(--accent)' : 'none',
                transition: 'background var(--dur-instant) var(--ease-standard)',
              }}
            >
              {columns.map((c) => (
                <td key={c.key} style={{
                  textAlign: c.align || 'left',
                  padding: '0 12px', height: rowH,
                  borderBottom: '1px solid var(--line)',
                  font: c.mono ? '500 13px/1.3 var(--font-mono)' : '400 13px/1.3 var(--font-sans)',
                  color: 'var(--ink-0)', whiteSpace: 'nowrap',
                }}>
                  {c.render ? c.render(r[c.key], r, i) : r[c.key]}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
