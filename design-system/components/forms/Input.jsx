import React from 'react';

/**
 * RallyOn Input — text field with optional uppercase mono label,
 * prefix/suffix adornment, and error state.
 */
export function Input({
  label,
  value,
  defaultValue,
  placeholder,
  type = 'text',
  size = 'md',
  disabled = false,
  invalid = false,
  hint,
  prefix = null,
  suffix = null,
  mono = false,
  onChange,
  style,
  ...rest
}) {
  const [focus, setFocus] = React.useState(false);
  const heights = { sm: 'var(--control-sm)', md: 'var(--control-md)', lg: 'var(--control-lg)' };
  const borderColor = invalid ? 'var(--status-danger)' : (focus ? 'var(--ink-0)' : 'var(--line)');

  return (
    <label style={{ display: 'flex', flexDirection: 'column', gap: '6px', ...style }}>
      {label && (
        <span style={{ font: 'var(--fw-semibold) 11px/1 var(--font-mono)', letterSpacing: 'var(--tracking-caps)', textTransform: 'uppercase', color: 'var(--ink-2)' }}>
          {label}
        </span>
      )}
      <div style={{
        display: 'flex', alignItems: 'center',
        height: heights[size],
        background: disabled ? 'var(--paper-0)' : 'var(--paper-2)',
        border: `${focus ? 'var(--bw-heavy)' : 'var(--bw)'} solid ${borderColor}`,
        borderRadius: 'var(--r-1)',
        padding: '0 10px', gap: '8px',
        transition: 'border-color var(--dur-1) var(--ease-standard)',
        boxShadow: focus && !invalid ? '0 0 0 2px var(--line-soft)' : 'none',
      }}>
        {prefix && <span style={{ color: 'var(--ink-2)', display: 'flex' }}>{prefix}</span>}
        <input
          type={type}
          value={value}
          defaultValue={defaultValue}
          placeholder={placeholder}
          disabled={disabled}
          onChange={onChange}
          onFocus={() => setFocus(true)}
          onBlur={() => setFocus(false)}
          style={{
            flex: 1, minWidth: 0, border: 'none', outline: 'none', background: 'transparent',
            font: mono ? 'var(--fw-medium) 13px/1.4 var(--font-mono)' : 'var(--fw-regular) 14px/1.4 var(--font-sans)',
            color: 'var(--ink-0)',
          }}
          {...rest}
        />
        {suffix && <span style={{ color: 'var(--ink-2)', display: 'flex', font: '600 12px/1 var(--font-mono)' }}>{suffix}</span>}
      </div>
      {hint && (
        <span style={{ font: '400 11px/1.3 var(--font-sans)', color: invalid ? 'var(--status-danger)' : 'var(--ink-2)' }}>{hint}</span>
      )}
    </label>
  );
}
