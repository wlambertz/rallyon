import * as React from 'react';

export interface DataTableColumn<Row = any> {
  key: string;
  header: React.ReactNode;
  /** CSS width (e.g. "80px", "20%"). */
  width?: string | number;
  align?: 'left' | 'center' | 'right';
  /** Render this column's cells in mono (IDs, scores, times). */
  mono?: boolean;
  /** Custom cell renderer. */
  render?: (value: any, row: Row, index: number) => React.ReactNode;
}

export interface DataTableProps<Row = any> extends React.HTMLAttributes<HTMLDivElement> {
  columns: DataTableColumn<Row>[];
  rows: Row[];
  /** Row click handler — enables hover accent seam + pointer. */
  onRowClick?: (row: Row, index: number) => void;
  getRowKey?: (row: Row, index: number) => React.Key;
  /** Tighter 34px rows. */
  compact?: boolean;
}

/**
 * Dense tabular data with a hard header seam and mono numeric columns.
 * @startingPoint section="Data" subtitle="Standings / manifest table" viewport="700x300"
 */
export function DataTable<Row = any>(props: DataTableProps<Row>): JSX.Element;
