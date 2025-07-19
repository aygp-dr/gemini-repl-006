FROM ubuntu:22.04

# Install basic dependencies
RUN apt-get update && apt-get install -y \
    curl \
    git \
    && rm -rf /var/lib/apt/lists/*

# Install mise (formerly rtx)
RUN curl https://mise.run | sh
ENV PATH="/root/.local/share/mise/shims:/root/.local/bin:${PATH}"

# Install tools via mise
RUN mise use --global java@temurin-17
RUN mise use --global babashka@latest

# Verify installations
RUN java --version && bb --version

WORKDIR /workspace

CMD ["/bin/bash"]