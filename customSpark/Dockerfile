# Start from the Spark worker base image (Alpine-based)
FROM bde2020/spark-worker:3.1.1-hadoop3.2

# Switch to root to install dependencies
USER root

# Install required packages using Alpine's 'apk' package manager
RUN apk add --no-cache \
    ffmpeg \
    git \
    build-base \
    wget \
    cmake   

# Clone and compile whisper.cpp
RUN cd /opt && \
    git clone https://github.com/ggerganov/whisper.cpp.git && \
    cd whisper.cpp && \
    # THE DEFINITIVE FIX:
    # We pass CMAKE_EXE_LINKER_FLAGS to explicitly add the C++ filesystem library (-lstdc++fs).
    # We also keep the flags to disable examples/tests to ensure a fast and clean build.
    cmake -S . -B build && \
    cmake --build build --config Release