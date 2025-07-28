package com.johncnstn.springbootdemo.softwareengineers.model;

import javax.validation.constraints.Size;
import java.util.List;

public record SoftwareEngineerDTO(Integer id, @Size(min = 2, max = 10) String name, List<String> techStack) {
}
