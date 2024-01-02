package com.example.socialapi.comments;

import com.example.socialapi.comments.dto.CommnetDTO;
import com.example.socialapi.comments.dto.CommentDTOMapper;
import com.example.socialapi.comments.request.CreateCommentReq;
import com.example.socialapi.comments.request.UpdateCommentReq;
import com.example.socialapi.user.UserRepository;
import com.example.socialapi.user.dto.EmbeddedUserMapper;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository repository;
    private final UserRepository userRepository;
    private final CommentDTOMapper mapper;
    private final EmbeddedUserMapper userMapper;

    public CommnetDTO createCmtEntityService(CreateCommentReq req) {
        Comment o = new Comment(userMapper.apply(userRepository.findUserById(new ObjectId(req.getUserId())).orElseThrow()),
                new ObjectId(req.getPostId()),
                req.getContent(),
                LocalDateTime.now());
        return mapper.apply(repository.save(o));
    }
    public CommnetDTO updateCmtEntityService(String id, UpdateCommentReq req) {
        Comment o = repository.findById(id).orElseThrow();
        o.setUser(userMapper.apply(userRepository.findUserById(new ObjectId(req.getUserId())).orElseThrow()));
        o.setPostId(new ObjectId(req.getPostId()));
        o.setContent(req.getContent());
        return mapper.apply(repository.save(o));
    }
    public List<CommnetDTO> getAllByPostId(ObjectId postId) {
        return repository.findAllByPostId(postId).orElseThrow()
                .stream()
                .map(mapper)
                .collect(Collectors.toList());
    }
    public String deleteCmt(String id) {
        Comment o = repository.findById(id).orElseThrow();
        repository.delete(o);
        boolean exist = repository.existsById(id);
        return exist ? "Failed" : "Deleted";
    }

}
