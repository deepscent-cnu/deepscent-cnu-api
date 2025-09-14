package deepscent_cnu.deepscent_cnu_api.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminPageController {

  @GetMapping("/login")
  public String adminLoginPage() {
    return "admin_login";
  }

  @GetMapping("/dashboard")
  public String adminDashBoardPage() {
    return "admin_dashboard";
  }

  @GetMapping("/member/{id}/slots")
  public String memberSlotsPage(@PathVariable("id") Long memberId, Model model) {
    model.addAttribute("targetMemberId", memberId);
    return "admin_member_slots";
  }

}
