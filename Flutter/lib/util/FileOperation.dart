import 'dart:convert';
import 'dart:io';
import 'package:dokit/kit/apm/apm.dart';
import 'package:path_provider/path_provider.dart';
import 'package:dokit/kit/apm/crash_kit.dart';

class FileUtil {
  static FileUtil shared = FileUtil._instance();
  FileUtil._instance();

  void writeCounter(String path, String content) async {
    var dateTime = DateTime.now();
    var time = dateTime.toString().substring(0, 19);
    var documentsDir = await getApplicationDocumentsDirectory();
    var documentsPath = documentsDir.path;
    var directory = Directory('$documentsPath/$path');
    if (!directory.existsSync()) {
      directory.createSync();
    }

    var file = File('$documentsPath/$path/$time.txt');
    if (!file.existsSync()) {
      file.createSync();
    }
    writeToFile(file, content);
  }

  //将数据内容写入指定文件中
  void writeToFile(File file, String notes) async {
    var newFile = await file.writeAsString(notes, encoding: utf8);
    if (newFile.existsSync()) {
      print('保存成功');
    }
  }

  ///删除文件
  Future<Object> deleteFile(String path) async {
    var documentsDir = await getApplicationDocumentsDirectory();
    var documentsPath = documentsDir.path;
    var directory = Directory('$documentsPath/$path');
    if (directory.existsSync()) {
      var fileList = directory.list();
      await for (FileSystemEntity fileSystemEntity in fileList) {
        await fileSystemEntity.delete();
      }
      return fileList;
    }
    return [];
  }

  ///遍历所有文件
  Future<Object> getAllSubFile(String path) async {
    var kit = ApmKitManager.instance.getKit(ApmKitName.KIT_CARSH);
    kit!.removeAllItem();
    var documentsDir = await getApplicationDocumentsDirectory();
    var documentsPath = documentsDir.path;
    var directory = Directory('$documentsPath/$path');
    var num = 0;
    if (directory.existsSync()) {
      var fileList = directory.list();
      await for (FileSystemEntity fileSystemEntity in fileList) {
        num++;
        if (num > 100) {
          //超过100条缓存记录 删除缓存
          await fileSystemEntity.delete();
        } else {
          var file = fileSystemEntity as File;
          var contents = file.readAsStringSync();
          CrashLogManager.instance.addLog(CrashLogBean.TYPE_ERROR, '$contents');
        }
      }
      return fileList;
    }
    return [];
  }
}
