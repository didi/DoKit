import 'package:vm_service/vm_service.dart';

class FieldOwnerNode {
  String? ownerName = '';
  String? ownerType = '';
  String? libraries;
  FieldOwnerNode({this.libraries, this.ownerName, this.ownerType});

  FieldOwnerNode.fromJson(Map<String, dynamic> json) {
    ownerName = json['ownerName'];
    ownerType = json['ownerType'];
    libraries = json['libraries'];
  }

  Map<String, dynamic> toJson() {
    return {
      'ownerName': ownerName,
      'ownerType': ownerType,
      'libraries': libraries,
    };
  }

  @override
  String toString() {
    return '{\nownerName:$ownerName\nownerType:$ownerType\nlibraries:"$libraries"\n}';
  }
}

// 闭包
class ClosureNode {
  String? functionName;
  String? closureOwner; //所属 可能是 方法、类、包
  String? ownerClass; // 如果owner是类=owner，owner是方法所在类
  String? libraries;
  int? funLine;
  int? funColumn;

  ClosureNode({
    this.functionName,
    this.closureOwner,
    this.ownerClass,
    this.libraries,
    this.funLine,
    this.funColumn,
  });

  ClosureNode.fromJson(Map<String, dynamic> json) {
    functionName = json['functionName'];
    closureOwner = json['closureOwner'];
    ownerClass = json['ownerClass'];
    libraries = json['libraries'];
    funLine = json['funLine'];
    funColumn = json['funColumn'];
  }

  Map<String, dynamic> toJson() {
    return {
      'functionName': functionName,
      'closureOwner': closureOwner,
      'ownerClass': ownerClass,
      'libraries': libraries,
      'funLine': funLine,
      'funColumn': funColumn,
    };
  }

  @override
  String toString() {
    return '{\nlibraries:"$libraries"\nclosureFunName:$functionName($funLine:$funColumn)\nowner:$closureOwner\nownerClass:$ownerClass\n}';
  }
}

class CodeLocation {
  String? code;
  int? lineNum;
  int? columnNum;
  String? className;
  String? uri;

  CodeLocation(
      this.code, this.lineNum, this.columnNum, this.className, this.uri);

  CodeLocation.fromJson(Map<String, dynamic> json) {
    code = json['code'];
    lineNum = json['lineNum'];
    columnNum = json['columnNum'];
    className = json['className'];
    uri = json['uri'];
  }

  Map<String, dynamic> toJson() {
    return {
      'code': code,
      'lineNum': lineNum,
      'columnNum': columnNum,
      'className': className,
      'uri': uri,
    };
  }

  String _subStr(String str) {
    final maxLen = 200;
    if (str.length > maxLen) {
      return str.substring(0, maxLen) + '...';
    }
    return str;
  }

  @override
  String toString() {
    var codeStr = _subStr(code ?? '');
    return '{\nsourceCode:"$codeStr"\nuri:"$uri#$lineNum:$columnNum"\n}';
  }
}

class LeaksMsgNode {
  String name = '';
  String? declaredType = '';
  String? parentField;
  String? libraries; // uri 路径
  String? parentKey; // if object in a Map, map's key
  int? parentListIndex; // if object in a List,it is index in the List
  FieldOwnerNode? fieldOwnerNode; // 如果当前node是Field类型，则给出其owner信息
  CodeLocation? codeLocation;
  ClosureNode? closureNode;
  RetainingObject? retainingObject;
  LeakedNodeType? leakedNodeType;

  LeaksMsgNode(this.name,
      {this.parentKey,
      this.parentListIndex,
      this.parentField,
      this.libraries,
      this.declaredType,
      this.fieldOwnerNode,
      this.codeLocation,
      this.retainingObject,
      this.closureNode,
      this.leakedNodeType});

  @override
  String toString() {
    return '{\nname:$name\nleakedNodeType:$leakedNodeType\nlibraries:"$libraries"\nfieldOwnerNode:${fieldOwnerNode.toString()}\ncodeLocation:${codeLocation.toString()}\nclosureNode:${closureNode.toString()}\n}';
  }

  Map<String, dynamic> toJson() {
    return {
      'name': name,
      'parentKey': parentKey,
      'parentListIndex': parentListIndex,
      'parentField': parentField,
      'libraries': libraries,
      'declaredType': declaredType,
      'fieldOwnerNode': fieldOwnerNode,
      'codeLocation': codeLocation,
      'closureNode': closureNode
    };
  }

  LeaksMsgNode.fromJson(Map<String, dynamic> json) {
    name = json['name'];
    parentKey = json['parentKey'];
    parentListIndex = json['parentListIndex'];

    parentField = json['parentField'];
    libraries = json['libraries'];
    declaredType = json['declaredType'];
    fieldOwnerNode = json['fieldOwnerNode'];
    codeLocation = json['codeLocation'];
    closureNode = json['closureNode'];
  }
}

class LeaksMsgInfo {
  List<LeaksMsgNode>? retainingPathList = [];
  int? leaksInstanceCounts;
  int? expectedTotalCount;
  String? leaksClsName;
  String? gcRootType;

  LeaksMsgInfo(
      List<LeaksMsgNode> this.retainingPathList, String this.gcRootType,
      {this.leaksInstanceCounts, this.leaksClsName, this.expectedTotalCount});

  @override
  String toString() {
    return '$leaksClsName : {\nleaksInstanceCounts: $leaksInstanceCounts, \nexpectedTotalCount: $expectedTotalCount, \nretainingPath: $retainingPathList, \ngcRootType: "$gcRootType" \n}';
  }
}

enum LeakedNodeType {
  unknown,
  widget,
  element,
}
