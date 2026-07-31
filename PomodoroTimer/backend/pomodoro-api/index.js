const cloudbase = require("@cloudbase/node-sdk");

const app = cloudbase.init({
  env: "pomodoro-dev-d1gghiq6p79a72113",
});
const db = app.rdb();

const TABLE_NAME = "focus_sessions";
const JSON_HEADERS = {
  "Content-Type": "application/json; charset=utf-8",
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "Content-Type",
  "Access-Control-Allow-Methods": "GET,POST,OPTIONS",
};

function jsonResponse(statusCode, body) {
  return {
    statusCode,
    headers: JSON_HEADERS,
    body: JSON.stringify(body),
  };
}

function parseRequestBody(event) {
  if (!event.body) {
    return {};
  }

  if (typeof event.body === "object") {
    return event.body;
  }

  const bodyText = event.isBase64Encoded
    ? Buffer.from(event.body, "base64").toString("utf8")
    : event.body;

  return JSON.parse(bodyText);
}

function isValidDate(value) {
  return typeof value === "string" && !Number.isNaN(Date.parse(value));
}

async function listFocusSessions() {
  const { data, error } = await db
    .from(TABLE_NAME)
    .select("id, task_name, duration_seconds, started_at, completed_at, created_at")
    .order("completed_at", { ascending: false })
    .limit(50);

  if (error) {
    throw error;
  }

  return jsonResponse(200, {
    ok: true,
    sessions: data,
  });
}

async function createFocusSession(event) {
  let body;
  try {
    body = parseRequestBody(event);
  } catch {
    return jsonResponse(400, {
      ok: false,
      message: "Request body must be valid JSON",
    });
  }

  const taskName = String(body.taskName ?? "").trim();
  const durationSeconds = Number(body.durationSeconds);
  const { startedAt, completedAt } = body;

  if (taskName.length < 1 || taskName.length > 120) {
    return jsonResponse(400, {
      ok: false,
      message: "taskName must contain 1 to 120 characters",
    });
  }

  if (
    !Number.isInteger(durationSeconds) ||
    durationSeconds < 1 ||
    durationSeconds > 43_200
  ) {
    return jsonResponse(400, {
      ok: false,
      message: "durationSeconds must be an integer from 1 to 43200",
    });
  }

  if (!isValidDate(startedAt) || !isValidDate(completedAt)) {
    return jsonResponse(400, {
      ok: false,
      message: "startedAt and completedAt must be ISO-8601 timestamps",
    });
  }

  if (Date.parse(startedAt) > Date.parse(completedAt)) {
    return jsonResponse(400, {
      ok: false,
      message: "completedAt must not be earlier than startedAt",
    });
  }

  const record = {
    task_name: taskName,
    duration_seconds: durationSeconds,
    started_at: new Date(startedAt).toISOString(),
    completed_at: new Date(completedAt).toISOString(),
  };
  const { error } = await db.from(TABLE_NAME).insert(record);

  if (error) {
    throw error;
  }

  return jsonResponse(201, {
    ok: true,
    session: record,
  });
}

exports.main = async (event = {}) => {
  const method = String(event.httpMethod ?? "GET").toUpperCase();
  const path = String(event.path ?? "/api/health");

  if (method === "OPTIONS") {
    return {
      statusCode: 204,
      headers: JSON_HEADERS,
      body: "",
    };
  }

  try {
    if (method === "GET" && path.endsWith("/health")) {
      return jsonResponse(200, {
        ok: true,
        service: "pomodoro-api",
        message: "Pomodoro backend is running",
        timestamp: new Date().toISOString(),
      });
    }

    if (path.endsWith("/focus-sessions") && method === "GET") {
      return await listFocusSessions();
    }

    if (path.endsWith("/focus-sessions") && method === "POST") {
      return await createFocusSession(event);
    }

    return jsonResponse(404, {
      ok: false,
      message: "Not found",
    });
  } catch (error) {
    console.error("Pomodoro API error", error);
    return jsonResponse(500, {
      ok: false,
      message: "Internal server error",
    });
  }
};
