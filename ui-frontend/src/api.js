export const publishQuery = async (query) => {
  const response = await fetch("/api/v1?q=" + query);
  return response;
};
