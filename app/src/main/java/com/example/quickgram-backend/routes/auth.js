import express from "express";
import bcrypt from "bcrypt";
import pool from "../db.js";

const router = express.Router();

// 🟢 Sign Up
router.post("/signup", async (req, res) => {
  const { email, password } = req.body;
  try {
    // hash password
    const hashedPassword = await bcrypt.hash(password, 12);

    // insert into quickgram_users
    const result = await pool.query(
      "INSERT INTO quickgram_users (email, password) VALUES ($1, $2) RETURNING id, email",
      [email, hashedPassword]
    );

    res.status(201).json({
      message: "User created successfully",
      user: result.rows[0],
    });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Error creating user" });
  }
});

// 🟡 Login
router.post("/login", async (req, res) => {
  const { email, password } = req.body;
  try {
    const result = await pool.query(
      "SELECT * FROM quickgram_users WHERE email = $1",
      [email]
    );

    if (result.rows.length === 0) {
      return res.status(400).json({ error: "Invalid email or password" });
    }

    const user = result.rows[0];
    const match = await bcrypt.compare(password, user.password);

    if (!match) {
      return res.status(400).json({ error: "Invalid email or password" });
    }

    res.json({
      message: "Login successful",
      user: { id: user.id, email: user.email },
    });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Error logging in" });
  }
});

export default router;
