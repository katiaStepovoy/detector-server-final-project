ARG PYTHON_VERSION=3.9.2
FROM python:${PYTHON_VERSION}-slim as base

# Prevents Python from writing pyc files.
ENV PYTHONDONTWRITEBYTECODE=1
ENV FLASK_APP="scripts/__init__.py"

WORKDIR /

# Download dependencies as a separate step to take advantage of Docker's caching.
# Leverage a cache mount to /root/.cache/pip to speed up subsequent builds.
# Leverage a bind mount to requirements.txt to avoid having to copy them into
# into this layer.
RUN --mount=type=cache,target=/root/.cache/pip \
    --mount=type=bind,source=requirements.txt,target=requirements.txt \
    python -m pip install -r requirements.txt \

RUN --mount=type=bind,source=scripts\ruled_based_algorithm\word_sentiment.xlsx , target=scripts\ruled_based_algorithm\word_sentiment.xlsx \

# Copy the source code into the container.
COPY . ./

# Expose the port that the application listens on.
EXPOSE 8083

RUN python -m nltk.downloader punkt
RUN python -m nltk.downloader stopwords
RUN python -m nltk.downloader wordnet

# Run the application.
CMD ["flask", "run","--host=0.0.0.0:8083"]