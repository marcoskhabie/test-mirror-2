import React, {Component} from 'react'
import './Topic.css';
import {RouteComponentProps, withRouter} from "react-router-dom";
import {TrendzButton} from "../common/TrendzButton/TrendzButton";
import ReactPaginate from "react-paginate";
import {parseJwt} from "../Routing/utils";
import Modal from "react-modal";
import {deleteTopic} from "../../api/TopicApi";

export type Props = RouteComponentProps<any> & {}

export type State = {
    posts: any[],
    showModal: boolean,
    currentPage: number
}

class Topic extends Component<Props, State> {

    constructor(props: Props) {
        super(props);
        this.state = {
            showModal: false,
            posts: [
                {id: 0, link: 'https://trello.com/c/pjHHwlbl/42-issue-0144-visualizacion-de-un-topic',title: 'This is the title of the post', author: 'Jhon Mark', description: 'This is the description for a humor post. asd asd asd asd sad asd as das das dasda sdasd asdasdasd asdasd asdas dasdasd asda sdas dasdasd asdas dasd asd as dasd asd asda.'},
                {id: 1, link: 'https://trello.com/c/pjHHwlbl/42-issue-0144-visualizacion-de-un-topic',title: 'This is the title of the post', author: 'Jhon Mark', description: 'This is the description for a humor post. asd asd asd asd sad asd as das das dasda sdasd asdasdasd asdasd asdas dasdasd asda sdas dasdasd asdas dasd asd as dasd asd asda.'},
                {id: 2, link: 'https://trello.com/c/pjHHwlbl/42-issue-0144-visualizacion-de-un-topic',title: 'This is the title of the post', author: 'Jhon Mark', description: 'This is the description for a humor post. asd asd asd asd sad asd as das das dasda sdasd asdasdasd asdasd asdas dasdasd asda sdas dasdasd asdas dasd asd as dasd asd asda.'},
                {id: 3, link: 'https://trello.com/c/pjHHwlbl/42-issue-0144-visualizacion-de-un-topic',title: 'This is the title of the post', author: 'Jhon Mark', description: 'This is the description for a humor post. asd asd asd asd sad asd as das das dasda sdasd asdasdasd asdasd asdas dasdasd asda sdas dasdasd asdas dasd asd as dasd asd asda.'},
                {id: 4, link: 'https://trello.com/c/pjHHwlbl/42-issue-0144-visualizacion-de-un-topic',title: 'This is the title of the post', author: 'Jhon Mark', description: 'This is the description for a humor post. asd asd asd asd sad asd as das das dasda sdasd asdasdasd asdasd asdas dasdasd asda sdas dasdasd asdas dasd asd as dasd asd asda.'},
                {id: 5, link: 'https://trello.com/c/pjHHwlbl/42-issue-0144-visualizacion-de-un-topic',title: 'This is the title of the post', author: 'Jhon Mark', description: 'This is the description for a humor post. asd asd asd asd sad asd as das das dasda sdasd asdasdasd asdasd asdas dasdasd asda sdas dasdasd asdas dasd asd as dasd asd asda.'},
                {id: 6, link: 'https://trello.com/c/pjHHwlbl/42-issue-0144-visualizacion-de-un-topic',title: 'This is the title of the post', author: 'Jhon Mark', description: 'This is the description for a humor post. asd asd asd asd sad asd as das das dasda sdasd asdasdasd asdasd asdas dasdasd asda sdas dasdasd asdas dasd asd as dasd asd asda.'},
                {id: 7, link: 'https://trello.com/c/pjHHwlbl/42-issue-0144-visualizacion-de-un-topic',title: 'This is the title of the post', author: 'Jhon Mark', description: 'This is the description for a humor post. asd asd asd asd sad asd as das das dasda sdasd asdasdasd asdasd asdas dasdasd asda sdas dasdasd asdas dasd asd as dasd asd asda.'},
                {id: 8, link: 'https://trello.com/c/pjHHwlbl/42-issue-0144-visualizacion-de-un-topic',title: 'This is the title of the post', author: 'Jhon Mark', description: 'This is the description for a humor post. asd asd asd asd sad asd as das das dasda sdasd asdasdasd asdasd asdas dasdasd asda sdas dasdasd asdas dasd asd as dasd asd asda.'},
                {id: 9, link: 'https://trello.com/c/pjHHwlbl/42-issue-0144-visualizacion-de-un-topic',title: 'This is the title of the post', author: 'Jhon Mark', description: 'This is the description for a humor post. asd asd asd asd sad asd as das das dasda sdasd asdasdasd asdasd asdas dasdasd asda sdas dasdasd asdas dasd asd as dasd asd asda.'},
                {id: 10, link: 'https://trello.com/c/pjHHwlbl/42-issue-0144-visualizacion-de-un-topic',title: 'This is the title of the post', author: 'Jhon Mark', description: 'This is the description for a humor post. asd asd asd asd sad asd as das das dasda sdasd asdasdasd asdasd asdas dasdasd asda sdas dasdasd asdas dasd asd as dasd asd asda.'},
                {id: 11, link: 'https://trello.com/c/pjHHwlbl/42-issue-0144-visualizacion-de-un-topic',title: 'This is the title of the post', author: 'Jhon Mark', description: 'This is the description for a humor post. asd asd asd asd sad asd as das das dasda sdasd asdasdasd asdasd asdas dasdasd asda sdas dasdasd asdas dasd asd as dasd asd asda.'},
            ],
            currentPage: 0
        }
    };

    componentDidMount() {

    }

    getTopics() {

    }

    handleCancel = () => {
        this.setState({showModal: false})
    }

    handleConfirm = () => {
        deleteTopic(this.props.location.state.topic.id).then(() => this.props.history.push('/main/home'))
    }

    handleDelete = () => {
        this.setState({showModal: true})
    }

    renderPosts = (currentPage: number) => {
        return this.state.posts.slice(currentPage*3, currentPage*3+3)
    }

    handlePageClick = (data: {selected: number}) => {
        this.setState({currentPage: data.selected})
    }

    render() {
        return (
            <div className={'topic-container'}>
                <Modal
                    isOpen={this.state.showModal}
                    onRequestClose={this.handleCancel.bind(this)}
                    shouldCloseOnOverlayClick={true}
                    className={'modal'}
                    overlayClassName={'overlay'}
                >
                    <div className={'modal-text'}>
                        <span>{'You are about to delete ' + this.props.location.state.topic.title + ' topic.'}</span>
                        <span>This action is irreversible, </span>
                        <span>do you wish to continue?</span>
                    </div>
                    <div className={'modal-buttons'}>
                        <TrendzButton title={'Confirm'} onClick={this.handleConfirm.bind(this)}/>
                        <TrendzButton title={'Cancel'} color={'#DF6052'} onClick={this.handleCancel.bind(this)}/>
                    </div>
                </Modal>
                <div className={'topic-header-wrapper'}>
                    <div className={'header-text'}>
                        <span className={'topic-title'}>{this.props.location.state.topic.title}</span>
                        <span className={'topic-subtitle'}>{this.props.location.state.topic.description}</span>
                    </div>
                    {
                        parseJwt(localStorage.getItem('token')).role.includes('ROLE_ADMIN') &&
                        <TrendzButton
                            title={'Delete topic'}
                            onClick={() => this.handleDelete()}
                            color={'#DF6052'}
                        />
                    }
                </div>
                <div className={'posts-container'}>
                    {
                        this.state.posts.length &&
                        this.renderPosts(this.state.currentPage).map((post) => (
                            <div className={'post-card-wrapper'}>
                                <div className={'post-card'}>
                                    <div className={'post-card-header'}>
                                        <div className={'post-card-title'}>
                                            <div className={'post-title'}>
                                                {post.title}
                                            </div>
                                            <div className={'post-author'}>
                                                {'by ' + post.author}
                                            </div>
                                        </div>
                                        <div className={'post-topic'}>
                                            {this.props.location.state.topic.title}
                                        </div>
                                    </div>
                                    <div className={'post-card-body'}>
                                        {post.description}
                                    </div>
                                    <div className={'post-card-footer'}>
                                        <a href={post.link}>{post.link}</a>
                                        <div className={'read-more'}>Read more</div>
                                    </div>
                                </div>
                            </div>
                        ))
                    }
                </div>
                <div className={'topic-footer'}>
                    <ReactPaginate
                        onPageChange={this.handlePageClick}
                        pageCount={this.state.posts.length/3}
                        pageRangeDisplayed={5}
                        marginPagesDisplayed={2}
                        previousLabel={"<"}
                        nextLabel={">"}
                        breakLabel={'...'}
                        containerClassName={"pagination"}
                        previousLinkClassName={"previous_page"}
                        nextLinkClassName={"next_page"}
                        disabledClassName={"disabled"}
                        activeClassName={"active"}
                    />
                </div>
            </div>
        )
    }
}

export default withRouter(Topic);
