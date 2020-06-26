//
//  MainThreadCheckerViewController.swift
//  DoraemonKit-Swift
//
//  Created by 邓锋 on 2020/6/22.
//

import Foundation

class MainThreadCheckerViewController: BaseViewController {
    
    private lazy var checkBtn: CellButton = {
        let btn = CellButton(frame: CGRect(x: 0, y: switchCell.bottom, width: self.view.width, height: 53))
        btn.renderUIWithTitle(title: LocalizedString("查看子线程UI记录"))
        btn.needDownLine()
        btn.delegate = self
        return btn
    }()
    private lazy var clearBtn: CellButton = {
        let btn = CellButton(frame: CGRect(x: 0, y: checkBtn.bottom, width: self.view.width, height: kSizeFrom750_Landscape(104)))
        btn.renderUIWithTitle(title: LocalizedString("一键清理子线程UI记录"))
        btn.needDownLine()
        btn.delegate = self
        return btn
    }()
    
    private lazy var switchCell: CellSwitch = {
        let frame = CGRect(x: 0, y: bigTitleView?.bottom ?? kSizeFrom750_Landscape(178), width: self.view.width, height: 53)
        let btn = CellSwitch(frame: frame)
        btn.renderUIWithTitle(title: LocalizedString("子线程UI检测开关"), on: MainThreadCheckerManager.mainThreadChecker)
        btn.needTopLine()
        btn.needDownLine()
        btn.delegate = self
        return btn
    }()

    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
    }
    
    override func needBigTitleView() -> Bool {
        return true
    }
    
    func setupUI() {
        setTitle(title: LocalizedString("子线程UI检测"))
        [switchCell, checkBtn, clearBtn].forEach { view.addSubview($0) }
    }

}

extension MainThreadCheckerViewController: CellSwitchDelegate {
    func changeSwitchOn(on: Bool) {
        MainThreadCheckerManager.mainThreadChecker = on
    }
}

extension MainThreadCheckerViewController: CellButtonDelegate {
    func cellBtnClick(sender: CellButton) {
        if sender == self.checkBtn {
            let listVc = FileListViewController.init(directory: MainThreadCheckerManager.default.directory)
            listVc.setTitle(title: LocalizedString("子线程UI列表"))
            navigationController?.pushViewController(listVc, animated: true)
        } else if sender == self.clearBtn {
            let alertController = UIAlertController(title: LocalizedString("提示"), message: LocalizedString("确认删除所有子线程UI记录吗？"), preferredStyle: .alert)
            let cancleAction = UIAlertAction(title: LocalizedString("取消"), style: .cancel, handler: nil)
            let okAction = UIAlertAction(title: LocalizedString("确定"), style: .default) { (_) in
                do {
                    try FileManager.default.removeItem(atPath: MainThreadCheckerManager.default.directory)
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
