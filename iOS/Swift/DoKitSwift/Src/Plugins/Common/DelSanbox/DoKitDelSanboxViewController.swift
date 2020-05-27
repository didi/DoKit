//
//  DoKitDelSanboxViewController.swift
//  AFNetworking
//
//  Created by didi on 2020/5/26.
//

import UIKit

class DoKitDelSanboxViewController: DoKitBaseViewController, DoKitCellButtonDelegate {

    var cellBtn: DoKitCellButton!
    override func viewDidLoad() {
        super.viewDidLoad()
        self.setTitle(title: DoKitLocalizedString("清理缓存"))
        
        cellBtn = DoKitCellButton(frame: CGRect(x: 0, y: self.bigTitleView!.bottom, width: self.view.width, height: kSizeFrom750_Landscape(104)))
        cellBtn.renderUIWithTitle(title: DoKitLocalizedString("清理缓存"))
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
    
    override func needBigTitleView() -> Bool {
        return true
    }
    
    func cellBtnClick(){
        
        DoKitAlertUtil.handleAlertAction(vc: self, title: DoKitLocalizedString("提示"), text: DoKitLocalizedString("确定要删除本地数据"), ok: DoKitLocalizedString("确定"), cancel: DoKitLocalizedString("取消"), okBlock: {
            self.cellBtn.renderUIWithRightContent(rightContent: DoKitLocalizedString("正在清理中"))
            DoKitUtil.clearLocalDatas()
            DispatchQueue.main.asyncAfter(deadline: DispatchTime.now()+1) {
                self.cellBtn.renderUIWithRightContent(rightContent: self.getHomeDirFileSize())
            }
        }) {
            
        }
    }

}
