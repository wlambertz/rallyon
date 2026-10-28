import React from 'react';

/**
 * RallyOn Select — native dropdown styled as a hard-seam control.
 */
export function Select({
  label,
  value,
  defaultValue,
  options = [],
  size = 'md',
  disabled = false,
  invalid = false,
  hint,
  onChange,
  style,
  ...rest
}) {
  const [focus, setFocus] = React.useState(false);
  const heights = { sm: 'var(--control-sm)', md: 'var(--control-md)', lg: 'var(--control-lg)' };
  const borderColor = invalid ? 'var(--status-danger)' : (focus ? 'var(--ink-0)' : 'var(--line)');

  const norm = options.map((o) => (typeof o === 'string' ? { value: o, label: o } : o));

  return (
    <label style={{ display: 'flex', flexDirection: 'column', gap: '6px', ...style }}>
      {label && (
        <span style={{ font: 'var(--fw-semibold) 11px/1 var(--font-mono)', letterSpacing: 'var(--tracking-caps)', textTransform: 'uppercase', color: 'var(--ink-2)' }}>
          {label}
        </span>
      )}
      <div style={{ position: 'relative', display: 'flex' }}>
        <select
          value={value}
          defaultValue={defaultValue}
          disabled={disabled}
          onChange={onChange}
          onFocus={() => setFocus(true)}
          onBlur={() => setFocus(false)}
          style={{
            appearance: 'none', WebkitAppearance: 'none',
            width: '100%', height: heights[size],
            padding: '0 34px 0 10px',
            background: disabled ? 'var(--paper-0)' : 'var(--paper-2)',
            border: `${focus ? 'var(--bw-heavy)' : 'var(--bw)'} solid ${borderColor}`,
            borderRadius: 'var(--r-1)',
            font: 'var(--fw-regular) 14px/1 var(--font-sans)',
            color: 'var(--ink-0)', cursor: disabled ? 'not-allowed' : 'pointer',
            outline: 'none',
            boxShadow: focus && !invalid ? '0 0 0 2px var(--line-soft)' : 'none',
            transition: 'border-color var(--dur-1) var(--ease-standard)',
          }}
          {...rest}
        >
          {norm.map((o) => (
            <option key={o.value} value={o.value}>{o.label}</option>
          ))}
        </select>
        <span style={{
          position: 'absolute', right: 10, top: '50%', transform: 'translateY(-50%)',
          pointerEvents: 'none', color: 'var(--ink-1)', font: '700 10px/1 var(--font-mono)',
        }}>▼</span>
      </div>
      {hint && (
        <span style={{ font: '400 11px/1.3 var(--font-sans)', color: invalid ? 'var(--status-danger)' : 'var(--ink-2)' }}>{hint}</span>
      )}
    </label>
  );
}
