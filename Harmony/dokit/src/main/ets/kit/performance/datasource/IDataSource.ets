import { LineData } from '../widget/LineData';

/**
 * 折线图数据源，对标 Android IDataSource
 */
export interface IDataSource {
  createData(): LineData;
}
