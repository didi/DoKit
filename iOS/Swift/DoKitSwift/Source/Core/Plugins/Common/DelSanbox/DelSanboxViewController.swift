//
//  DoKitDelSanboxViewController.swift
//  DoraemonKit-Swift
//
//  Created by didi on 2020/5/26.
//

import UIKit

class DelSanboxViewController: BaseViewController, CellButtonDelegate {

    var cellBtn: CellButton!
    override func viewDidLoad() {
        super.viewDidLoad()
        self.setTitle(title: LocalizedString("清理缓存"))
        
        cellBtn = CellButton(frame: CGRect(x: 0, y: self.bigTitleView!.bottom, width: self.view.width, height: kSizeFrom750_Landscape(104)))
        cellBtn.renderUIWithTitle(title: LocalizedString("清理缓存"))
        cellBtn.renderUIWithRightContent(rightContent: getHomeDirFileSize())
        cellBtn.delegate = self
        cellBtn.needDownLine()
        view.addSubview(cellBtn)
    }
    
    func getHomeDirFileSize() -> String {
        let homeDir = NSHomeDirectory()
        let util = DoKitUtil()
        util.getFileSizeWithPath(path: homeDir)
        let fileSize = util.fileSize
        let fileSizeString = ByteCountFormatter.string(fromByteCount: Int64(fileSize), countStyle: .file)
        return fileSizeString
    }
    
    func cellBtnClick(){
        
        AlertUtil.handleAlertAction(vc: self, title: LocalizedString("提示"), text: LocalizedString("确定要删除本地数据"), ok: LocalizedString("确定"), cancel: LocalizedString("取消"), okBlock: {
            self.cellBtn.renderUIWithRightContent(rightContent: LocalizedString("正在清理中"))
            DoKitUtil.clearLocalDatas()
            DispatchQueue.main.asyncAfter(deadline: DispatchTime.now()+1) {
                self.cellBtn.renderUIWithRightContent(rightContent: self.getHomeDirFileSize())
            }
        }) {
            
        }
    }

}
