//
//  UserDefaultsEditViewController.swift
//  DoraemonKit-Swift
//
//  Created by Smallfly on 2020/7/28.
//

import UIKit

class UserDefaultsEditViewController: BaseViewController {

    let model: UserDefaultsModel
    
    init(with model: UserDefaultsModel) {
        self.model = model
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        title = model.key
        view.backgroundColor = .white
        view.addSubview(textView)
        
        textView.text = model.value
        
        let edit = UIBarButtonItem(barButtonSystemItem: .done, target: self, action: #selector(commit))
        navigationItem.rightBarButtonItem = edit
    }
    
    @objc func commit() {
        let content = textView.text
        UserDefaults.standard.set(content, forKey: model.key)
        navigationController?.popViewController(animated: true)
    }
    
    // MARK: -
    
    lazy var textView: UITextView = {
        let textView = UITextView(frame: UIScreen.main.bounds)
        return textView
    } ()
}
