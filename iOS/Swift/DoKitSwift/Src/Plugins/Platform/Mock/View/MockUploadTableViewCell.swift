//
//  MockUploadTableViewCell.swift
//  DoraemonKit-Swift
//
//  Created by 郝振壹 on 2020/6/29.
//

import UIKit

protocol MockUploadTableViewCellModel: MockTableViewCellModel {}

class MockUploadTableViewCell: MockTableViewCell {

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    override func update(_ data: MockTableViewCellModel) {
        guard let data = data as? MockUploadTableViewCellModel else { return }
    }

}
