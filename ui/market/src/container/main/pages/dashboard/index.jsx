import React from 'react'
import { Upload, Icon, message, Button } from 'antd';
import httpServer from 'api/httpServer';

const Dragger = Upload.Dragger;



export default class Dashboard extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            defaultFileList: [],
            fileList: [],
            uploading: false,

        }
    }

    onChange = (info) => {
        const status = info.file.status;
        if (status === 'uploading') {
            console.log(info.file, info.fileList);
        } else {
            // console.log('111111111', info.file.response.data);
        }
        if (status === 'done') {
            message.success(`${info.file.name} file uploaded successfully.`);
        } else if (status === 'error') {
            message.error(`${info.file.name} file upload failed.`);
        }
    }
    render() {
        const props = {
            name: 'file',
            listType: 'picture',
            multiple: true,
            action: '/api/upload',
            defaultFileList: this.state.defaultFileList,
            onChange: this.onChange,
            onRemove: (file) => {
                this.setState(({ fileList }) => {
                    const index = fileList.indexOf(file);
                    const newFileList = fileList.slice();
                    newFileList.splice(index, 1);
                    return {
                        fileList: newFileList,
                    };
                });
            },
            beforeUpload: (file) => {
                this.setState(({ fileList }) => ({
                    fileList: [...fileList, file],
                }));
                return true;
            },
            fileList: this.state.fileList,
        };
        return (

            <div>
                <Dragger {...props}>
                    <p className="ant-upload-drag-icon">
                        <Icon type="inbox" />
                    </p>
                    <p className="ant-upload-text">Click or drag file to this area to upload</p>
                    <p className="ant-upload-hint">Support for a single or bulk upload. Strictly prohibit from uploading company data or other band files</p>
                </Dragger>

                <a href="/api/download">下载</a>
            </div>

        )
    }
}
