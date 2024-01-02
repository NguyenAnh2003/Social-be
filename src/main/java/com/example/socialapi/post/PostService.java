package com.example.socialapi.post;

import com.example.socialapi.category.CategoryRepository;
import com.example.socialapi.category.dto.EmbeddedCategoryMapper;
import com.example.socialapi.follow.FollowService;
import com.example.socialapi.follow.dto.FollowDTO;
import com.example.socialapi.post.dto.PostDTO;
import com.example.socialapi.post.dto.PostDTOMapper;
import com.example.socialapi.user.UserRepository;
import com.example.socialapi.user.dto.EmbeddedUserMapper;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository repository;
    private final CategoryRepository cateRepository;
    private final UserRepository userRepository;
    private final PostDTOMapper mapper;
    private final EmbeddedUserMapper userMapper;
    private final EmbeddedCategoryMapper categoryMapper;
    private final FollowService followService;
    private static final Logger logging = LoggerFactory.getLogger(PostService.class);

    /* create */
    public PostDTO createPostService(String userId, String categoryId,
                                     String title, String description,
                                     String imageURL) {
        logging.info("creating post in service");
        Post post = new Post(userMapper.apply(userRepository.findUserById(new ObjectId(userId)).orElseThrow(() -> new UsernameNotFoundException("User not found"))),
                categoryMapper.apply(cateRepository.findById(categoryId).orElseThrow()),
                title,
                description,
                imageURL, LocalDateTime.now());
        return mapper.apply(repository.save(post));
    }

    /* update */
    public PostDTO updatePostService(String id, String userId,
                                     String categoryId, String title,
                                     String description, String imageURL) {
        logging.info("update post with postId", id);
        Post post = repository.findById(id).orElseThrow();
        post.setCategory(categoryMapper.apply(cateRepository.findById(categoryId).orElseThrow()));
        post.setTitle(title);
        post.setDescription(description);
        post.setImageURL(imageURL);
        // set
        return mapper.apply(repository.save(post));
    }

    /* read */
    public List<PostDTO> getAll(String userId) {
        logging.info("get all posts by userId and followingUserId");
        List<Post> listOfPosts = new ArrayList<Post>();
        listOfPosts.clear();
        // define posts array for result fetching
        List<Post> posts = repository.findAllByUserId(userId).orElseThrow();
        if(posts.isEmpty()) return Collections.emptyList();
        else listOfPosts.addAll(posts); // add all posts fetched by userId
        List<Post> followingUsersPosts = new ArrayList<Post>(); // following users posts
        List<FollowDTO> followingUsers = followService.getFollowingUsers(userId); // get following users by userId
        if(followingUsers.isEmpty()) return Collections.emptyList();
        else {followingUsers.forEach(
            follow -> {
                followingUsersPosts.addAll(repository.findAllByUserId(String.valueOf(follow.getFollowingId())).orElseThrow());
                listOfPosts.addAll(followingUsersPosts); // add to result
                followingUsersPosts.clear(); // remove init array after adding
            }
        );}
        return listOfPosts.isEmpty() ? Collections.emptyList() : listOfPosts.stream().map(mapper).collect(Collectors.toList());
    }

    public PostDTO getPostById(String id) {
        logging.info("get single post by postId");
        return mapper.apply(repository.findById(id).orElseThrow());
    }

    public List<PostDTO> getPostsByUserId(String userId) {
        logging.info("get all posts by userId");
        return repository.findAllByUserId(userId).orElseThrow()
                .stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    /* delete */
    public Boolean deletePost(String id) {
        logging.info("deleting post", id);
        Post p = repository.findById(id).orElseThrow();
        repository.delete(p);
        boolean existed = repository.existsById(id);
        return existed;
    }

}
