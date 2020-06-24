//
//  ANRViewController.swift
//  DoraemonKit-Swift
//
//  Created by gengwenming on 2020/6/20.
//

import UIKit

class ANRViewController: BaseViewController {
    
    private var checkBtn: CellButton?
    private var clearBtn: CellButton?

    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
    }
    
    override var needBigTitleView: Bool {
        return true
    }
    
    func setupUI() {
        set(title: LocalizedString("卡顿检测"))
        
        let swhFrame = CGRect(x: 0, y: bigTitleView?.bottom ?? kSizeFrom750_Landscape(178), width: view.width, height: CellSwitch.defaultHeight)
        let switchCell = CellSwitch(frame: swhFrame)
        switchCell.renderUIWithTitle(title: LocalizedString("卡顿检测开关"), on: ANRManager.sharedInstance.isOn)
        switchCell.needTopLine()
        switchCell.needDownLine()
        switchCell.delegate = self
        
        let checkBtn = CellButton(frame: CGRect(x: 0, y: switchCell.bottom, width: view.width, height: 53))
        checkBtn.renderUIWithTitle(title: LocalizedString("查看卡顿记录"))
        checkBtn.needDownLine()
        checkBtn.delegate = self
        self.checkBtn = checkBtn
        
        let clearBtn = CellButton(frame: CGRect(x: 0, y: checkBtn.bottom, width: view.width, height: kSizeFrom750_Landscape(104)))
        clearBtn.renderUIWithTitle(title: LocalizedString("一键清理卡顿记录"))
        clearBtn.needDownLine()
        clearBtn.delegate = self
        self.clearBtn = clearBtn
        
        [switchCell, checkBtn, clearBtn].forEach { view.addSubview($0) }
    }

}

extension ANRViewController: CellSwitchDelegate {
    func changeSwitchOn(on: Bool) {
        ANRManager.sharedInstance.isOn = on
        if on {
            ANRManager.sharedInstance.start()
        } else {
            ANRManager.sharedInstance.stop()
        }
    }
}

extension ANRViewController: CellButtonDelegate {
    func cellBtnClick(sender: CellButton) {
        if sender == self.checkBtn {
            let listVc = ANRListViewController()
            navigationController?.pushViewController(listVc, animated: true)
        } else if sender == self.clearBtn {
            let alertController = UIAlertController(title: LocalizedString("提示"), message: LocalizedString("确认删除所有卡顿记录吗？"), preferredStyle: .alert)
            let cancleAction = UIAlertAction(title: LocalizedString("取消"), style: .cancel, handler: nil)
            let okAction = UIAlertAction(title: LocalizedString("确定"), style: .default) { (_) in
                do {
                    try FileManager.default.removeItem(atPath: ANRTool.anrDirectory)
                    ToastUtil.showToast(LocalizedString("删除成功"), superView: self.view)
                } catch {
                    print("removeItem error: \(error.localizedDescription)")
                    ToastUtil.showToast(LocalizedString("删除失败"), superView: self.view)
                }
            }
            alertController.addAction(cancleAction)
            alertController.addAction(okAction)
            present(alertController, animated: true, completion: nil)
        }
    }
}
