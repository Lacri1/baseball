import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import "../styles/PostEditForm2.css";
const PostEditForm2 = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const [post, setPost] = useState({});

  const { writer, title, body } = post;

  const onChange = (event) => {
    const { value, name } = event.target;
    setPost((prevPost) => ({
      ...prevPost,
      [name]: value,
    }));
  };

  const getPost = async () => {
    try {
      const response = await axios.get(
        `http://localhost:8080/board?writingId=${id}`
      );
      setPost(response.data);
    } catch (error) {
      console.error("불러오지 못함", error);
    }
  };
  const backToPost = () => {
    navigate("/board/" + id);
  };
  const updatePost = async () => {
    try {
      const response = await axios.put(
        `http://localhost:8080/board/${id}`,
        post
      );
      console.log(post);
      if (response.status === 200) {
        alert("수정되었습니다.");
        navigate("/board/" + id);
      } else {
        throw new Error("게시물 수정 실패");
      }
    } catch (error) {
      console.error("Error updating board:", error);
      alert("게시물 수정에 실패했습니다.");
    }
  };

  useEffect(() => {
    getPost();
  }, []);
  return (
      <div className="container">
        <div className="input-group">
    <div>
      <h2 class style={{ textAlign: "center" }}>
        글 수정하기
      </h2>
          <span>제목</span>
          <input
            type="text"
            name="title"
            placeholder="제목"
            value={title}
            onChange={onChange}
          />
          <span> 작성자</span>
          <input
            type="text"
            name="writer"
            placeholder="작성자"
            value={writer}
            readOnly={true}
          />
          <br />

          <br />
          <textarea
            placeholder="내용"
            name="body"
            cols="48"
            rows="30"
            value={body}
            onChange={onChange}
          />
          <br />
          <div className="button-group">
            <button onClick={updatePost}>수정완료</button>
            <button onClick={backToPost}>수정취소</button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PostEditForm2;