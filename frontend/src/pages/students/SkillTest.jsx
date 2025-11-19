import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import axios from 'axios';
import { Card, CardContent, CardHeader, CardTitle } from '../../components/ui/card';
import { Button } from '../../components/ui/button';
import { ArrowLeft, Loader2, CheckCircle2, XCircle, AlertCircle } from 'lucide-react';

const SkillTest = () => {
  const { roleName, skillName } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [test, setTest] = useState(null);
  const [answers, setAnswers] = useState({});
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [testStarted, setTestStarted] = useState(false);
  const [submitted, setSubmitted] = useState(false);

  useEffect(() => {
    if (!user?.id || !roleName || !skillName) return;

    const fetchTest = async () => {
      try {
        const decodedRoleName = decodeURIComponent(roleName || '');
        const decodedSkillName = decodeURIComponent(skillName || '');
        const response = await axios.get(
          `http://localhost:8080/api/skill-test/${encodeURIComponent(decodedRoleName)}/${encodeURIComponent(decodedSkillName)}?studentId=${user.id}`
        );
        setTest(response.data);
        setTestStarted(response.data?.status === "in_progress" || response.data?.status === "completed");
        setSubmitted(response.data?.status === "completed");
        
        // Initialize answers from existing test
        if (response.data?.questions) {
          const existingAnswers = {};
          response.data.questions.forEach(q => {
            if (q.selectedAnswer) {
              existingAnswers[q.questionId] = q.selectedAnswer;
            }
          });
          setAnswers(existingAnswers);
        }
      } catch (err) {
        console.error('Error fetching test:', err);
        setError('Failed to load test');
      } finally {
        setLoading(false);
      }
    };

    fetchTest();
  }, [user?.id, roleName, skillName]);

  const handleStartTest = async () => {
    try {
      const decodedRoleName = decodeURIComponent(roleName || '');
      const decodedSkillName = decodeURIComponent(skillName || '');
      const response = await axios.post(
        `http://localhost:8080/api/skill-test/${encodeURIComponent(decodedRoleName)}/${encodeURIComponent(decodedSkillName)}/start?studentId=${user.id}`
      );
      setTest(response.data);
      setTestStarted(true);
    } catch (err) {
      console.error('Error starting test:', err);
      setError('Failed to start test');
    }
  };

  const handleAnswerChange = (questionId, answer) => {
    setAnswers(prev => ({
      ...prev,
      [questionId]: answer
    }));
  };

  const handleSubmit = async () => {
    if (!test?.questions) return;

    setSubmitting(true);
    try {
      const decodedRoleName = decodeURIComponent(roleName || '');
      const decodedSkillName = decodeURIComponent(skillName || '');
      
      // Prepare answered questions
      const answeredQuestions = test.questions.map(q => ({
        ...q,
        selectedAnswer: answers[q.questionId] || null
      }));

      const response = await axios.post(
        `http://localhost:8080/api/skill-test/${encodeURIComponent(decodedRoleName)}/${encodeURIComponent(decodedSkillName)}/submit?studentId=${user.id}`,
        answeredQuestions
      );
      
      setTest(response.data);
      setSubmitted(true);
      
      // Navigate back to job description after a delay
      setTimeout(() => {
        navigate(`/students/career-blueprint/role/${encodeURIComponent(decodedRoleName)}`);
      }, 3000);
    } catch (err) {
      console.error('Error submitting test:', err);
      setError('Failed to submit test');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-blue-600" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <Card className="w-full max-w-md">
          <CardContent className="pt-6">
            <div className="flex items-center gap-2 text-red-600 mb-4">
              <AlertCircle className="h-5 w-5" />
              <p>{error}</p>
            </div>
            <Button onClick={() => navigate(-1)} className="w-full">
              <ArrowLeft className="mr-2 h-4 w-4" />
              Go Back
            </Button>
          </CardContent>
        </Card>
      </div>
    );
  }

  if (!testStarted && !submitted) {
    return (
      <div className="min-h-screen bg-gray-50 py-8">
        <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8">
          <Card>
            <CardHeader>
              <CardTitle>Skill Test: {decodeURIComponent(skillName)}</CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-gray-600 mb-4">
                This test will assess your knowledge of {decodeURIComponent(skillName)} for the role of {decodeURIComponent(roleName)}.
              </p>
              <p className="text-gray-600 mb-6">
                The test contains {test?.totalQuestions || 0} questions. You need to score at least 70% to pass.
              </p>
              <Button onClick={handleStartTest} className="w-full">
                Start Test
              </Button>
            </CardContent>
          </Card>
        </div>
      </div>
    );
  }

  if (submitted && test) {
    const percentage = test.totalQuestions > 0 ? (test.score / test.totalQuestions) * 100 : 0;
    const passed = percentage >= 70;

    return (
      <div className="min-h-screen bg-gray-50 py-8">
        <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                {passed ? (
                  <CheckCircle2 className="h-6 w-6 text-green-600" />
                ) : (
                  <XCircle className="h-6 w-6 text-red-600" />
                )}
                Test Results
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-center mb-6">
                <div className={`text-4xl font-bold mb-2 ${passed ? 'text-green-600' : 'text-red-600'}`}>
                  {percentage.toFixed(1)}%
                </div>
                <p className="text-gray-600">
                  Score: {test.score} out of {test.totalQuestions}
                </p>
                <p className={`mt-2 font-semibold ${passed ? 'text-green-600' : 'text-red-600'}`}>
                  {passed ? 'Congratulations! You passed!' : 'You did not pass. Please try again.'}
                </p>
              </div>
              
              <div className="space-y-4 mb-6">
                {test.questions?.map((question, index) => (
                  <div key={question.questionId} className="border rounded-lg p-4">
                    <p className="font-semibold mb-2">
                      Question {index + 1}: {question.questionText}
                    </p>
                    <div className="space-y-2">
                      {question.options?.map((option, optIndex) => (
                        <div
                          key={optIndex}
                          className={`p-2 rounded ${
                            option === question.correctAnswer
                              ? 'bg-green-100 border-green-500'
                              : option === question.selectedAnswer && option !== question.correctAnswer
                              ? 'bg-red-100 border-red-500'
                              : 'bg-gray-50'
                          }`}
                        >
                          {option}
                          {option === question.correctAnswer && (
                            <CheckCircle2 className="inline ml-2 h-4 w-4 text-green-600" />
                          )}
                        </div>
                      ))}
                    </div>
                  </div>
                ))}
              </div>

              <p className="text-sm text-gray-600 text-center">
                Redirecting to role details...
              </p>
            </CardContent>
          </Card>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="mb-6">
          <Button variant="ghost" onClick={() => navigate(-1)}>
            <ArrowLeft className="mr-2 h-4 w-4" />
            Back
          </Button>
        </div>

        <Card className="mb-6">
          <CardHeader>
            <CardTitle>Skill Test: {decodeURIComponent(skillName)}</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-gray-600 mb-4">
              Answer all questions to complete the test.
            </p>
          </CardContent>
        </Card>

        <div className="space-y-6">
          {test?.questions?.map((question, index) => (
            <Card key={question.questionId}>
              <CardHeader>
                <CardTitle className="text-lg">
                  Question {index + 1}
                </CardTitle>
              </CardHeader>
              <CardContent>
                <p className="mb-4">{question.questionText}</p>
                <div className="space-y-2">
                  {question.options?.map((option, optIndex) => (
                    <label
                      key={optIndex}
                      className="flex items-center p-3 border rounded-lg cursor-pointer hover:bg-gray-50"
                    >
                      <input
                        type="radio"
                        name={question.questionId}
                        value={option}
                        checked={answers[question.questionId] === option}
                        onChange={() => handleAnswerChange(question.questionId, option)}
                        className="mr-3"
                      />
                      {option}
                    </label>
                  ))}
                </div>
              </CardContent>
            </Card>
          ))}
        </div>

        <div className="mt-6 flex gap-4">
          <Button
            onClick={handleSubmit}
            disabled={submitting || Object.keys(answers).length < (test?.totalQuestions || 0)}
            className="flex-1"
          >
            {submitting ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                Submitting...
              </>
            ) : (
              'Submit Test'
            )}
          </Button>
        </div>
      </div>
    </div>
  );
};

export default SkillTest;

