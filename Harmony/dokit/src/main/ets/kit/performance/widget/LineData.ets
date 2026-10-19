/**
 * 折线点数据，对标 Android LineData
 */
export class LineData {
  value: number;
  label: string | null;

  constructor(value: number, label: string | null) {
    this.value = value;
    this.label = label;
  }

  static obtain(value: number, label: string | null): LineData {
    return new LineData(value, label);
  }
}
