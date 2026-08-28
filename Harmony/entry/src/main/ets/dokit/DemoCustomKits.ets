import { AbstractKit } from '@didi/dokit';
import { DemoKit } from './demo/DemoKit';
import { TestSimpleDokitFloatViewKit } from './demo/TestSimpleDokitFloatViewKit';
import { TestSimpleDokitFragmentKit } from './demo/TestSimpleDokitFragmentKit';

/**
 * Demo 自定义业务 Kit，对标 Android App.kt mapKits
 */
export function buildDemoCustomKits(): Map<string, AbstractKit[]> {
  const map: Map<string, AbstractKit[]> = new Map();

  const zone1: AbstractKit[] = [
    new DemoKit(),
    new TestSimpleDokitFloatViewKit(),
    new TestSimpleDokitFragmentKit()
  ];
  map.set('业务专区1', zone1);

  const zone2: AbstractKit[] = [
    new DemoKit()
  ];
  map.set('业务专区2', zone2);

  return map;
}
