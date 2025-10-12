package deepscent_cnu.deepscent_cnu_api.device_info.repository;

import deepscent_cnu.deepscent_cnu_api.auth.entity.Member;
import deepscent_cnu.deepscent_cnu_api.device_info.entity.DeviceInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRegisterRepository extends JpaRepository<DeviceInfo, Long> {

  Optional<DeviceInfo> findByMember(Member member);
}
