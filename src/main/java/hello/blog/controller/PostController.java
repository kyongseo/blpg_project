package hello.blog.controller;

import hello.blog.domain.Post;
import hello.blog.domain.User;
import hello.blog.service.PostService;
import hello.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final UserService userService;

    /**
     * 게시글 등록
     */
    @GetMapping("/create")
    public String showCreatePost(Model model) {
        model.addAttribute("post", new Post());
        return "/post/createPost";
    }

    @PostMapping("/create")
    public String createPost(@RequestParam("title") String title,
                             @RequestParam("content") String content,
                             @CookieValue(value = "username", defaultValue = "") String username) {
//        System.out.println("Title: " + title);
//        System.out.println("Content: " + content);
        if (!username.isEmpty()) {
            // 사용자 이름(userName)을 이용하여 게시물 등록 작업 수행
            postService.createPost(username, title, content);
        } else {
            // 사용자 정보가 없는 경우 처리할 코드
            return "redirect:/loginform";
        }
        return "redirect:/";
    }

//    @PostMapping("/create")
//    public String processForm(@RequestParam("title") String title,
//                              @RequestParam("content") String content,
//                              HttpServletRequest request) {
//
//        // HttpServletRequest를 통해 쿠키 배열을 가져온다
//        Cookie[] cookies = request.getCookies();
//        //추가함
//        //
//
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if (cookie.getName().equals("userName")) {
//                    String userName = cookie.getValue();
//
//                    // 사용자 이름(userName)을 이용하여 게시물 등록 등의 작업을 수행
//                    System.out.println("User name from cookie: " + userName);
//
//                    // 여기서 게시물 등록 로직을 호출하도록 구현
//                    postService.createPost(title, content, userName);
//                    break;
//                }
//            }
//        } else {
//            // 쿠키가 존재하지 않는 경우 처리할 코드
//            System.out.println("No cookies found");
//        }
//        return "redirect:/";
//    }

    /**
     * 게시글 수정
     */
    @GetMapping("/{postId}/edit")
    public String showEditPost(@PathVariable("postId") Long postId,
                               Model model) {
        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        model.addAttribute("post", post);
        return "/post/editPost";
    }

    @PostMapping("/{postId}/edit")
    public String editPost(@PathVariable("postId") Long postId,
                           @RequestParam("title") String title,
                           @RequestParam("content") String content) {
        postService.updatePost(postId, title, content);
        return "redirect:/posts/" + postId; // 수정된 게시글로 리디렉션
    }

    /**
     * 상세 페이지
     */
    @GetMapping("/{postId}")
    public String getPostById(@PathVariable("postId") Long postId, Model model,
                              @CookieValue(value = "username", defaultValue = "") String username) {

        Post post = postService.getPostById(postId)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));
        Optional<User> userOptional = userService.findByUserName(username);
        User user = userOptional.get();

        model.addAttribute("post", post);
        model.addAttribute("username", user.getUserName());
        model.addAttribute("profileImage", "/files/" + userOptional.get().getFilename());
        return "/post/viewPost";
    }

    /**
     * 게시글 삭제
     */
//    @DeleteMapping("/{postId}/delete")
//    public String deletePost(@PathVariable("postId") Long postId) {
//
//        postService.deletePostById(postId);
//        return "redirect:/";
//    }

    @PostMapping("/{postId}/delete")
    public String deletePost(@PathVariable("postId") Long postId, @RequestParam("_method") String method) {
        if ("delete".equalsIgnoreCase(method)) {
            postService.deletePostById(postId);
        }
        return "redirect:/";
    }
}