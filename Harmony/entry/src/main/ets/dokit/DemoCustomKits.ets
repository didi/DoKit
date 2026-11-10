import { AbstractKit } from '@didi-hummer/dokit';
import { DemoKit } from './DemoKit';
import { TestSimpleDokitFloatViewKit } from './TestSimpleDokitFloatViewKit';
import { TestSimpleDokitFragmentKit } from './TestSimpleDokitFragmentKit';
import { MultiPageKit } from './MultiPageKit';
import { FloatWindow1Kit } from './FloatWindow1Kit';

/**
 * Demo 自定义业务 Kit，对标 Android App.kt mapKits
 */
export function buildDemoCustomKits(): Map<string, AbstractKit[]> {
  const map: Map<string, AbstractKit[]> = new Map();

  const zone1: AbstractKit[] = [
    new DemoKit(),
    new TestSimpleDokitFloatViewKit(),
    new TestSimpleDokitFragmentKit(),
    new MultiPageKit(),
    new FloatWindow1Kit()
  ];
  map.set('业务专区1', zone1);

  const zone2: AbstractKit[] = [
    new DemoKit()
  ];
  map.set('业务专区2', zone2);

  return map;
}
